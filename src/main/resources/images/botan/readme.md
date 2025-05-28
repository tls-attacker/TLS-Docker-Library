## BOTAN
### Versions 
	1.11.3, 1.11.4, 1.11.5, 1.11.6, 
	1.11.8, 1.11.9, 1.11.10, 1.11.11, 1.11.12, 1.11.13, 
	1.11.14, 1.11.15, 1.11.16, 1.11.17, 1.11.19, 1.11.20, 1.11.21, 1.11.22, 1.11.23, 1.11.24, 1.11.30, 1.11.31, 1.11.32, 1.11.33, 
	1.11.34,
	1.11.25, 1.11.26, 1.11.27, 1.11.28, 1.11.29, 
	2.0.0, 2.0.1, 2.1.0, 2.2.0, 2.3.0, 2.4.0, 2.5.0, 2.6.0, 2.7.0, 2.8.0, 2.9.0, 2.10.0
	3.0.0, 3.1.0, 3.1.1, 3.2.0, 3.3.0, 3.4.0, 3.5.0, 3.6.0, 3.6.1, 3.7.0, 3.7.1, 3.8.0, 3.8.1

- server successfully tested: 1.11.6, 1.11.13, 1.11.33, 1.11.34, 1.11.29, 2.0.0, 2.1.0, 2.2.0, 2.3.0
- clients successfully tested: 1.11.33, 1.11.34, 1.11.29, 2.0.0, 2.1.0, 2.2.0, 2.3.0
- server and client versions failed to build (have to check build process): 1.11.8-1.11.13
- clients failed test (have to check parameters): 1.11.6, 1.11.13
- Not supported Botan Versions:
  - all <= 1.11.2
  - 1.1.7
  - 1.1.18

### Build
- all versions: `python3 build.py -l botan`
- latest version: `python3 build.py -l botan:latest`
- specific version: `python3 build.py -l botan:VERSION`

### Run
- run tls client:
  - `docker run botan-client:VERSION localhost --port=4433 --skip-system-cert-store --ignore-cert-error`

- Start server versions < 1_11_32:
  - `docker run -v cert-data:/cert/ -it botan-server:VERSION /cert/rsa2048cert.pem /cert/rsa2048key.pem --port=4433`

- Start server versions >= 1_11_32:
  - `docker run -v cert-data:/cert/ -it botan-server:VERSION /cert/rsa2048cert.pem /cert/rsa2048key.pem --port=4433 --policy=/compat.txt`
