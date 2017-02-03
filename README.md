# happy-chameleon

[![Build Status](https://travis-ci.org/CK35/happy-chameleon.svg?branch=master)](https://travis-ci.org/CK35/happy-chameleon)
[![Coverage Status](https://coveralls.io/repos/github/CK35/happy-chameleon/badge.svg?branch=master)](https://coveralls.io/github/CK35/happy-chameleon?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-centralde.ck35.raspberry.happy.chameleon/happy-chameleon/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g%3Ade.ck35.raspberry.happy.chameleon)


#####How to build and install on RaspberryPi2
Build a Debian package with Maven. Navigate into the root project folder and run:
```
mvn clean package
```
The Debian package is created here:
```
deb/target/happy-chameleon-deb_1.0.0_all.deb
```
Copy package to your Raspberry and install it with dpkg
```
dpkg -i happy-chameleon-deb_1.0.0_all.deb
```
If necessary convert the file from the DOS / MAC format to the UNIX format
```
dos2unix /etc/init.d/happy-chameleon
```
Now you are ready to start
```
/etc/init.d/happy-chameleon start
```
If everything was ok you can invoke the following REST resource
```
curl -i "http://raspberrypi:8080/devices/status"
```

#####DHT Sensor drivers from Adafruit
This project uses the DHT sensor drivers from [Adafruit](https://github.com/adafruit/Adafruit_Python_DHT):

>Adafruit Python DHT Sensor Library
>
>Python library to read the DHT series of humidity and temperature sensors on a Raspberry Pi or Beaglebone Black.
>
>Designed specifically to work with the Adafruit DHT series sensors ----> https://www.adafruit.com/products/385
>
>Currently the library is only tested with Python 2.6/2.7.
>
>For all platforms (Raspberry Pi and Beaglebone Black) make sure your system is able to compile Python extensions. On Raspbian or Beaglebone Black's Debian/Ubuntu image you can >ensure your system is ready by executing:
>
>sudo apt-get update
>sudo apt-get install build-essential python-dev
>
>Install the library by downloading with the download link on the right, unzipping the archive, and executing:
>
>sudo python setup.py install
>
>You can ommit the sudo if you use raspberry pi.
>
>See example of usage in the examples folder.
>
>Adafruit invests time and resources providing this open source code, please support Adafruit and open-source hardware by purchasing products from Adafruit!
>
>Written by Tony DiCola for Adafruit Industries.
>MIT license, all text above must be included in any redistribution