## OPENSSL

### Versions

        0.9.7, 0.9.7a, 0.9.7b, 0.9.7c, 0.9.7d, 0.9.7e, 0.9.7f, 0.9.7g, 0.9.7h, 0.9.7i, 0.9.7j, 0.9.7k, 0.9.7l, 0.9.7m,
        0.9.8, 0.9.8a, 0.9.8b, 0.9.8c, 0.9.8d, 0.9.8e, 0.9.8f, 0.9.8g, 0.9.8h, 0.9.8i, 0.9.8j, 0.9.8k, 0.9.8l, 
        0.9.8m, 0.9.8n, 0.9.8o, 0.9.8p, 0.9.8q, 0.9.8r, 0.9.8s, 0.9.8t, 0.9.8u, 0.9.8v, 0.9.8w, 0.9.8x, 0.9.8y, 0.9.8za, 0.9.8zb, 0.9.8zc, 0.9.8zd, 0.9.8ze, 0.9.8zf, 0.9.8zg, 0.9.8zh, 0.9.8m-beta1,
        1.0.0, 1.0.0a, 1.0.0b, 1.0.0c, 1.0.0d, 1.0.0e, 1.0.0f, 1.0.0g, 1.0.0h, 1.0.0i, 1.0.0j, 1.0.0k, 1.0.0l, 1.0.0m, 1.0.0n, 1.0.0o, 1.0.0p, 1.0.0q, 1.0.0r, 1.0.0s, 1.0.0-beta5,
        1.0.0-beta1, 1.0.0-beta2, 1.0.0-beta3, 1.0.0-beta4,
        1.0.1, 1.0.1a, 1.0.1b, 1.0.1c, 1.0.1d, 1.0.1e, 1.0.1f, 1.0.1g, 1.0.1h, 1.0.1i, 1.0.1j, 1.0.1k, 1.0.1l, 1.0.1-beta1, 1.0.1-beta2, 1.0.1-beta3, 
        1.0.1m, 1.0.1n, 1.0.1o, 1.0.1p, 1.0.1q, 1.0.1r, 1.0.1s, 1.0.1t, 1.0.1u,
        1.0.2, 1.0.2-beta1, 1.0.2-beta2, 1.0.2-beta3,
        1.0.2a, 1.0.2b, 1.0.2c, 1.0.2d, 1.0.2e, 1.0.2f, 1.0.2g, 1.0.2h, 1.0.2i, 1.0.2j, 1.0.2k, 1.0.2l,	 
        1.1.0, 1.1.0a, 1.1.0b, 1.1.0c, 1.1.0d, 1.1.0e, 1.1.0f, 1.1.0-pre3,
        1.1.1-pre2 - 1.1.1w
        3.0.0 - 3.5.0

- server successfully tested: 0.9.7, 0.9.8, 0.9.8m, 0.9.8m-beta1, 1.0.0, 1.0.0-beta1, 1.0.1, 1.0.1m, 1.0.2, 1.0.2a, 1.1.0
- clients successfully tested: 0.9.7, 0.9.8, 0.9.8m, 0.9.8m-beta1, 1.0.0, 1.0.0-beta1, 1.0.1, 1.0.1m, 1.0.2, 1.0.2-beta1, 1.1.0

### Build

- all versions: `python3 build.py -l openssl`
- latest version: `python3 build.py -l openssl:latest`
- specific version: `python3 build.py -l openssl:VERSION`

### Run

- run tls server:
  - `docker run -it --rm -v cert-data:/cert/ openssl-server:VERSION -accept 4433 -cert /cert/rsa2048cert.pem -key /cert/rsa2048key.pem -loop`
- run tls client:
  - `docker run -it --rm openssl-client:VERSION -connect localhost:4433`

