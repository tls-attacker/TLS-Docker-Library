#!/bin/sh

array=(a b c d e f -pre3)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.0${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_0${array[$i]}-server -f Dockerfile-1_1_0x .
	i=i+1
done

#docker build --build-arg VERSION=-pre1 -t openssl-1_1_0-pre1-server -f Dockerfile-1_1_0-pre1and2 .
#todo:
#../libcrypto.a(async.o): In function `async_fibre_swapcontext.constprop.2':
#async.c:(.text+0x36): undefined reference to `setcontext'
#../libcrypto.a(async_posix.o): In function `async_fibre_makecontext':
#async_posix.c:(.text+0xb5): undefined reference to `getcontext'
#async_posix.c:(.text+0x11a): undefined reference to `makecontext'
#collect2: error: ld returned 1 exit status
#make[2]: *** [../Makefile.shared:164: link_app.] Error 1
#make[1]: *** [Makefile:141: openssl] Error 2
#make: *** [Makefile:294: build_apps] Error 1
#docker build --build-arg VERSION=-pre2 -t openssl-1_1_0-pre2-server -f Dockerfile-1_1_0-pre1and2 .