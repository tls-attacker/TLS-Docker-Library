After running setup.sh, you have a docker volume with keys and certificate data.
You can add this volume to the container with: 
```bash
-v cert-data:/cert/:ro,nocopy
```

The volume conains the following files and certificates:
- /cert/ec256key.pem and /cert/ec256cert.pem (alias is cert)
- /cert/rsa2048key.pem and /cert/rsa2048cert.pem (alias is cert)
- /cert/keys.jks (aliases are ec256 and rsa2048)
All passwords are password

For example, you can run a TLS server with the following command:
```bash
docker run -it -v cert-data:/cert/:ro,nocopy --rm openssl-server -key /cert/ec256key.pem -cert /cert/ec256cert.pem
```
