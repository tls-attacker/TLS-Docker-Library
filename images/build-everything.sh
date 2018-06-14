cd baseimage;
./build-base-image.sh
./build-base-image-ubuntu.sh
cd ..
cd bearssl
./bearssl-0.X.sh
cd ..
cd boringssl
./boringssl-X.sh
cd ..
cd botan
./botan-1_11_X.sh
./botan-2.X.sh
cd ..
cd bouncycastle
./bouncycastletls.sh
cd ..
cd DamnVulnerableOpenSSL
./damnvulnerableopenssl.sh
cd ..
cd gnutls
./gnutls.sh
cd ..
#cd go
#./go.sh
#cd ..
cd jsse
./jsse.sh
cd ..
cd libressl
./libressl.sh
cd ..
cd matrixssl
./matrixssl.sh
cd ..
cd mbed
./mbedtls.sh
./polarssl.sh
cd ..
#cd nss
#./nss.sh
#cd ..
cd ocaml-tls
./ocamltls.sh
cd ..
cd openssl
./openssl-build_all.sh
cd ..
#cd rustls
#./rustls.sh
#cd ..
cd s2n
./s2n.sh
cd ..
cd wolfssl
./cyassl.sh
./wolfssl.sh
cd ..
