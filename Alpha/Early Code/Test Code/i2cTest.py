import smbus
import time

bus = smbus.SMBus(1)
address = 0x77
cmd1 = 1
cmd2 = 2
cmd3 = 3

data = [98,99,100]
#Python I2C test with Teensy 3.1

while True:
	rawVal = bus.read_byte_data(address, cmd1)
	print(rawVal)
	time.sleep(1)

	rawVals = bus.read_i2c_block_data(address, cmd2)
	print(rawVals)
	time.sleep(1)

	bus.write_i2c_block_data(address, cmd3, data)
	print("sent 3 bytes")
	time.sleep(1)
