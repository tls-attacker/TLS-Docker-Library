## BORINGSSL

### Versions

        2272, 2311, 2357, 
        2490, 2564, 2623, 2661, 3239, 
        2704, 2883, 2924, 2987, 3029, 3112, 3202, 3239, 3282, 3359, 3538, 3945
        chromium-stable, main

- boringSSL stopped versioning their releases, instead you are supposed to always use the latest main branch
  - there are however periodic snapshots (TODO?)
- server successfully tested: all versions
- clients successfully tested: all versions

### Build

- all versions: `python3 build.py -l boringssl`
- latest version: `python3 build.py -l boringssl:latest`
- specific version: `python3 build.py -l boringssl:VERSION`

### Run

- run tls server:
  - `docker run -it --rm -v cert-data:/cert/ boringssl-server:VERSION -accept 4433 -cert /cert/rsa2048cert.pem -key /cert/rsa2048key.pem -loop`
- run tls shim:
  - `docker run -it --rm --entrypoint /bin/bssl_shim boringssl-server:VERSION`
- run tls client:
  - `docker run -it --rm boringssl-client:VERSION -connect localhost:4433`

