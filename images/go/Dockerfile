FROM golang:1.9-alpine as go

ADD tls_server.go /go/src/tls_server/tls_server.go
RUN CGO_ENABLED=0 go install tls_server

FROM scratch
COPY --from=go /go/bin/tls_server /bin/tls_server
ENTRYPOINT ["server-entrypoint", "/bin/tls_server"]
