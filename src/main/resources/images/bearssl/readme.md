## BEARSSL

### Versions

        0.4
        0.5
        master

- server successfully tested: all versions
- clients successfully tested: all versions

### Build

- all versions: `python3 build.py -l bearssl`
- latest version: `python3 build.py -l bearssl:latest`
- specific version: `python3 build.py -l bearssl:VERSION`

### Run

- run tls server:
  - `docker run -v cert-data:/certs/ bearssl-server:VERSION -cert /certs/rsa2048cert.pem -key /certs/rsa2048key.pem -p 8443`
- run tls client:
  - `docker run bearssl-client:VERSION localhost:8443`

