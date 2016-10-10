#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <sys/un.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <linux/types.h>
#include <linux/netlink.h>
#include <errno.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stddef.h>
#include <stdexcept>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include <utility>

#include <string>
#include <iostream>
#include <sstream>
#include <map>

using namespace std;

#define BUFFER_SIZE 1024

#define PORT 8150

#define ADD 0
#define REMOVE 1
#define ERROR -1

map<int, pid_t> dpMap;

//初始化和内核通信的socket
int initHotplugSock()
{
    const int buffersize = 1024;
	int ret;
	struct sockaddr_nl snl;
	bzero(&snl, sizeof(struct sockaddr_nl));
	snl.nl_family = AF_NETLINK;
	snl.nl_pid = getpid();
	snl.nl_groups = 1;

	int sock = socket(PF_NETLINK, SOCK_DGRAM, NETLINK_KOBJECT_UEVENT);
    if (sock == -1)
    {
       	perror("socket in HotPlug failed");
       	exit(0);
    }
    setsockopt(sock, SOL_SOCKET, SO_RCVBUF, &buffersize, sizeof(buffersize));

    ret = bind(sock, (struct sockaddr *)&snl, sizeof(struct sockaddr_nl));
    if (ret < 0)
    {
       	perror("bind in HotPlug failed");
      	close(sock);
        exit(0);
    }
    return sock;
}

int getInfoType(string info)
{
    if(info.find("add") != string::npos)
        return ADD;
    else if(info.find("remove") != string::npos)
        return REMOVE;
    else
        return ERROR;
}

void analyzeDmesg(int& usbCom, string& usbId)
{
    FILE *pstream = popen("dmesg -c","r");
	char buf[BUFFER_SIZE] = {0};
	while(fgets(buf,sizeof(buf),pstream)!=NULL)
	{
		string info(buf);

		int usbPos = info.find("ttyUSB");
		if(usbPos != string::npos)
            usbCom = (int)info[usbPos + 6] - '0';

		int vPos = info.find("idVendor");
		int pPos = info.find("idProduct");
		if(vPos != string::npos && pPos != string::npos)
            usbId = info.substr(vPos+9, 4) + info.substr(pPos+10, 4);
	}
	pclose(pstream);
}

int main(int argc, char** argv)
{
    system("dmesg -c");

    //socket初始化
    int hotplugSock = initHotplugSock();

    stringstream ss;

    while(true)
    {
        char buf[BUFFER_SIZE] ={0};
        recv(hotplugSock, &buf, sizeof(buf), 0);
		string info(buf);
		//cout << "Recv msg: " << info << endl;

        int usbCom = 0;
        string usbId = "";

        analyzeDmesg(usbCom, usbId);
        int type = getInfoType(info);

        if(usbCom >= 0)
        {
            if(type == ADD && usbId.length() == 8)
            {
				cout << "UsbId: " << usbId << endl;;
				cout << "Add on ttyUSB" << usbCom << endl;
                pid_t pid = fork();
                if(pid < 0)
                    cout << "Fork failed." << endl;
                else if(pid == 0)
                {
                    //serach in config file
                    string usbComStr, usbSpeedStr;
                    ss << usbCom;
                    ss >> usbComStr;
                    ss.clear();
                    ss << 115200;
                    ss >> usbSpeedStr;
                    ss.clear();
                    execl("usb.o", usbComStr.c_str(), usbSpeedStr.c_str(), usbId.c_str(), NULL);
                    break;
                }
                else
                    dpMap[usbCom] = pid;
            }
            else if(type == REMOVE && dpMap.find(usbCom) != dpMap.end())
            {
				cout << "Remove on ttyUSB" << usbCom << endl;
				kill(dpMap[usbCom], SIGTERM);
				dpMap.erase(usbCom);
            }
		}
    }
    return 0;
}
