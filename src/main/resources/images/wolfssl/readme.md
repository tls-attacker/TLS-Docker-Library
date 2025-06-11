## WOLFSSL
### Versions 
	cyassl:
		 2.9.4, 2.9.2, 2.9.1, 2.9.0, 2.8.6, 2.8.5, 2.8.5a, 2.8.4, 2.8.3, 2.8.2, 2.8.0, 2.7.2, 2.7.0, 2.6.2, 2.6.0, 2.5.2b, 2.5.0, 2.4.7, 2.4.6, 2.4.2, 2.4.0, 2.3.0
	wolfssl:
		3.15.8, 3.15.7-stable, 3.15.6, 3.15.5a, 3.15.5-stable, 3.15.3-stable, 3.15.0-stable, 3.14.5, 3.14.4, 3.14.2, 3.14.0b, 3.14.0a, 3.14.0-stable, 3.13.3, 3.13.2, 3.13.0-stable
		3.12.2-stable, 3.12.0-stable, 3.11.0-stable, 3.10.4, 3.10.3, 3.10.2-stable, 3.10.0a, 3.10.0-stable, 3.9.10b, 3.9.10-stable, 3.9.8, 3.9.6w, 3.9.6, 3.9.1, 3.9.0, 3.7.0, 3.6.9d, 3.6.9c, 3.6.9b, 3.6.9, 3.6.8, 3.6.6, 3.6.2, 3.6.0b, 3.6.0, 3.4.8, 3.4.6, 3.4.2, 3.4.0, 3.3.3,
		3.3.2, 3.3.0, 3.2.6, 3.2.4, 3.2.0, 3.1.0, 3.0.2, 3.0.0,
		4.0.0-stable, 4.1.0-stable, 4.2.0-stable, 4.2.0c, 4.3.0-stable, 4.4.0-stable, 4.5.0-stable, 4.6.0-stable, 4.7.0-stable, 4.8.0-stable, 4.8.1-stable
		5.0.0-stable, 5.1.0-stable, 5.2.0-stable, 5.2.1-stable, 5.3.0-stable, 5.4.0-stable, 5.5.0-stable, 5.5.1-stable, 5.5.2-stable, 5.5.3-stable, 5.5.4-stable, 5.6.0-stable, 5.6.4-stable, 5.6.6-stable, 5.7.0-stable, 5.7.2-stable, 5.7.4-stable, 5.7.6-stable, 5.8.0-stable
- server successfully tested: wolfssl-3.12.2-stable, wolfssl-3.3.2, cyassl-2.9.4
- clients successfully tested: wolfssl-3.12.2-stable, wolfssl-3.3.2, cyassl-2.9.4

### Build
note: old cyassl versions are also built and run with the name wolfssl
- all versions: `python3 build.py -l wolfssl`
- latest version: `python3 build.py -l wolfssl:latest`
- specific version: `python3 build.py -l wolfssl:VERSION`

### Run
- run tls server:
	- `docker run -it --rm wolfssl-server:latest -p 4433 -b -i`
- run tls client:
	- `docker run -it --rm wolfssl-client:VERSION -h localhost -p 4433 -d`