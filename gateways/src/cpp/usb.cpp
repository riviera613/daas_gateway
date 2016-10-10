#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>


#include <iostream>
using namespace std;

#define PORT 8150

int usbfd;

int sockfd;

int openUsb(char com)
{
    string path("/dev/ttyUSB");
    path.push_back(com);
    return open(path.c_str(), O_RDWR|O_NOCTTY);
}

int configUsb(int fd,int nSpeed, int nBits, char nEvent, int nStop)
{
    struct termios newtio,oldtio;
    if(tcgetattr(fd,&oldtio) != 0)
        return -1;
    bzero(&newtio, sizeof(newtio));
    newtio.c_cflag |= CLOCAL | CREAD;
    newtio.c_cflag &= ~CSIZE;

    switch(nBits)
    {
    case 7: newtio.c_cflag |= CS7; break;
    case 8: newtio.c_cflag |= CS8; break;
    default: return -1;
    }

    switch(nEvent)
    {
    case 'O':
        newtio.c_cflag |= PARENB;
        newtio.c_cflag |= PARODD;
        newtio.c_iflag |= (INPCK | ISTRIP);
        break;
    case 'E':
        newtio.c_iflag |= (INPCK | ISTRIP);
        newtio.c_cflag |= PARENB;
        newtio.c_cflag &= ~PARODD;
        break;
    case 'N': newtio.c_cflag &= ~PARENB; break;
    default: return -1;
    }

    int speed;
	switch(nSpeed)
    {
    case 2400: speed = B2400; break;
    case 4800: speed = B4800; break;
    case 9600: speed = B9600; break;
    case 38400: speed = B38400; break;
    case 57600: speed = B57600; break;
    case 115200: speed = B115200; break;
    default: return -1;
    }
    cfsetispeed(&newtio, speed);
    cfsetospeed(&newtio, speed);

    if(nStop == 1)
        newtio.c_cflag &= ~CSTOPB;
    else if(nStop == 2)
        newtio.c_cflag |=  CSTOPB;
    newtio.c_cc[VTIME]  = 1;
    newtio.c_cc[VMIN] = 50;
    tcflush(fd,TCIFLUSH);
    return tcsetattr(fd,TCSANOW,&newtio)!=0 ? -1 : 0;
}

int initNetSock()
{
    int sock = socket(AF_INET,SOCK_STREAM, 0);

    struct sockaddr_in servaddr;
    bzero(&servaddr,sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(PORT);
    servaddr.sin_addr.s_addr= inet_addr("127.0.0.1");

    if(connect(sock, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("connect");
        exit(0);
    }
    else
        return sock;
}

void* recvMsg(void* argc)
{
    while(true)
    {
        char buf[128];
        int l = recv(sockfd, buf, sizeof(buf), 0);
        write(usbfd, buf, sizeof(buf));
        buf[l] = '\0';
        cout << "Client Recv " << buf << endl;
    }
}

int main(int argc, char** argv)
{
    if(argc < 3)
	{
		perror("Error argc");
		exit(0);
	}

	char usbCom = argv[argc - 3][0];
    int usbSpeed = atoi(argv[argc - 2]);
    string usbId(argv[argc - 1]);
    cout << "UsbCom: " << usbCom << endl << "UsbSpeed: " << usbSpeed << endl;
    cout << "UsbId: " << usbId << endl;

    //USB init
    usbfd = openUsb(usbCom);
    if(usbfd < 0)
    {
        perror("Open USB failed");
        exit(0);
    }
    if(configUsb(usbfd, usbSpeed, 8, 'N', 1) < 0)
    {
        perror("Config USB failed");
        exit(0);
    }

    //TCP socket init
    sockfd = initNetSock();

    //login
    string loginStr = "login#usb#" + usbId;
    send(sockfd, loginStr.c_str(), loginStr.length(), 0);
    char loginAck[128];
	int ackLength = recv(sockfd, loginAck, sizeof(loginAck), 0);
	loginAck[ackLength] = '\0';
	string loginAckStr(loginAck);
    cout << "Client Recv " << loginAck << endl;
	if(loginAckStr == "success")
    {
		cout << "Client Login Success" << endl;
        //server -> client
        pthread_t id;
        pthread_create(&id, NULL, recvMsg, NULL);

        //client -> server
        while(true)
        {
            char buf[128];
            int l = read(usbfd, buf, 128);
            buf[l] = '\0';
            cout << "Client Send: " << buf << endl;
            send(sockfd, buf, l, 0);
        }
    }
    else
        cout << "Client Login failed" << endl;

    return 0;
}

