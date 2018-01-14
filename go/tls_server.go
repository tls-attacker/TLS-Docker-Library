package main

import (
	"crypto/tls"
	"flag"
	"io"
	"log"
	"net"
	"os"
	"strconv"
	"strings"
)

func main() {
	certPath := flag.String("cert", "", "Comma-separated list of PEM cert paths")
	keyPath := flag.String("key", "", "Comma-separated list of PEM key paths")
	port := flag.Int("port", 4433, "Server port")
	flag.Parse()

	if *certPath == "" {
		flag.Usage()
		os.Exit(1)
	}

	certs := strings.Split(*certPath, ",")
	keys := strings.Split(*keyPath, ",")
	if len(certs) != len(keys) {
		log.Fatal("to each key must correspond a certificate")
	}

	config := &tls.Config{}
	config.PreferServerCipherSuites = true
	for i := range certs {
		cert, err := tls.LoadX509KeyPair(certs[i], keys[i])
		if err != nil {
			log.Fatal(err)
		}
		config.Certificates = append(config.Certificates, cert)
	}

	l, err := tls.Listen("tcp", net.JoinHostPort("0.0.0.0", strconv.Itoa(*port)), config)
	if err != nil {
		log.Fatal(err)
	}
	for {
		conn, err := l.Accept()
		if err != nil {
			log.Println("Accept error:", err)
		}
		go func(c *tls.Conn) {
			for {
				buf := make([]byte, 128)
				n, err := c.Read(buf)
				if err != nil {
					if err != io.EOF {
						log.Println("Read error:", err)
					}
					return
				}
				_, err = c.Write(buf[:n])
				if err != nil {
					if err != io.EOF {
						log.Println("Write error:", err)
					}
					return
				}
			}
		}(conn.(*tls.Conn))
	}
}
