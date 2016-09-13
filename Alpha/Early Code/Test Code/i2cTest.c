#include <wiringPiI2C.h>
#include <stdio.h>

int ret;
int fd;
int testCMD = 1;

void main(){
	fd = wiringPiI2CSetup(0x77);
	
	ret = wiringPiI2CWrite(fd, testCMD);
	printf("%d\n", ret);
	sleep(1);
	printf("%d", wiringPiI2CRead(fd));
	printf(" %d\n", wiringPiI2CRead(fd));
}
