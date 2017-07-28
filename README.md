# SimpleExchangeProxyServer
A proxy server for Microsoft Exchange that allows rewrite request from the client to Exchange or response from Exchange back. 
This project compatible with RPC OVER HTTP protocol, which is used by Microsoft Outlook. Demand that requires proxy all 
Exchange protocol stack includes OWA, EWS, RPC, OAB, ActiveSync, etc.  can use this project to do so.

## Why?
In my scenario, I need to rewrite the OWA logon request to support the token-base authentication from the web front end. When I trying
to use Nginx to do so, I found the OUTLOOK can't work anymore because the communication protocol(RPC) between the OUTLOOK and the EXCHANGE is
an irregular HTTP protocol format that the verb is RPC_IN_DATA or RPC_OUT_DATA. So I guess the Nginx discards such the request.

## So?
So, This project can proxy communication between client and EXCHANGE server that from the regular port(typically 80 and 443). The project
using a Man-In-Middle model to work. It can separate the RPC protocol and regular HTTP protocols(OWA,EWS,ActiveSync..) from the low-level stream, then
you can rewrite the regular HTTP req/res and directly transmit the RPC message between the client and the server. Currently, the project does
not support rewriting the RPC message, it just transmitted directly.

## Attation
Currently, the project is not high-performance-ready(it using synchronize I/O and a CachedThreadPool), and maybe not full-security-ready too(the memory
of the server may contain sensitive message that does not erase)

## How to use

1. You need to import a depnedency from this project (using Maven):
        
        $ git clone git@github.com:AxeQiu/SimpleExchangeProxyServer.git

        $ cd SimpleExchangeProxyServer

        $ mvn clean install

        then, put the dependency to your own project:

        <dependency>

            <groupId>io.pingpang</groupId>

            <artifactId>SimpleExchangeProxyServer</artifactId>

            <version>1.0</version>

        </dependency

2. Making the Acceptor

    The Acceptor represents a socket acceptor that accept the connect request from the client side.

        //port 80 proxy

        Acceptor acceptor80 = new Acceptor(
        
            new InetSocketAddress(
            
                InegAddress.getByName(serverAddress), 80)); // which the serverAddress is
                your proxy server ip address or hostname
        

        //port 443 proxy

        Acceptor acceptor443 = new Acceptor(
        
            new InetSocketAddress(
                InetAddress.getByName(serverAddress), 443)); // which the serverAddress is
                your proxy server ip address or hostname

        //The 443 port transmits the http message that enabled the SSL/TLS, so you must initialize your own
        
        //SSLContext using your own keystore that contains the private key of your organization

        SSLContext sslCtx = SomeSSLContextFactory.getCtx();  // which the SomeSSLContextFactory is your own
        class that this framework not contains.

        acceptor443.setSslContext(sslCtx);

3. Making the Connector

    The Connector represents a socket connector that connect to the exchange server

    //port 80

    final Connector connector80 = new Connector(
    
        InetAddress.getByName(exAddress), 80); //which the exAddress is your Exchange server ip or hostname

    //port 443
    
    final Connector connector443 = new Connector(
        InetAddress.getByName(exAddress), 443); //which the exAddress is your Exchange server ip or hostname

    //Same the Acceptor of the 443 port, the connector serve the 443 port need set a same SSLContext instance.
        
    connector443.setSslContext(sslContext);

    //wrap the connector to the Router

    Router route80 = new Router() { 
    
        @Override

        public Connector getConnector(InetAddress addr) {

            return connector80;

        }

    }



    Router route443 = new Router() {
        
        @Override

        public Connector getConnector(InetAddress addr) {

            return connector443;

        }

    }

4. Testing

    Because two socket(80 and 443) need to be proxy, here need two SimpleExchangeProxyServer classes

    //80 proxy server

    SimpleExchangeProxyServer p80 = new SimpleExchangeProxyServer();
    
    p80.setAcceptor(acceptor80);

    p80.setRoutable(route80);



    //443 proxy server

    SimpleExchangeProxyServer p443 = new SimpleExchangeProxyServer();

    p443.setAcceptor(acceptor443);

    p443.setRoutable(route443);



    //startup

    //443 proxy startup

    new Thread() {
    
        @Override

        public void run() {

            p443.start();

        }

    }.start();


    //80 proxy startup
    p80.start();

    

    If everything all right, the server will proxy the communication between client and Exchange server.
    You can access the proxy server address instead of access the exchange server address directly


5. Rewriting
    

    If the test is OK, from here begin rewrite the request. I will demonstrate rewrite the owa logon request



    //ExchangeRequestLine represents the first line of Http protocol, such "POST /owa/auth.owa"

    ExchangeRequestLine owaLogonReq = new ExchangeRequestLine();

    owaLogonReq.setVerb("POST"); //here is case sensitive

    owaLogonReq.setPath("/owa/auth\\.owa"); //here can using regular express

    //RequestHandle interface represent the method of rewriting
    
    RequestHandle handle = new RequestHandle() {

        @Override

        public boolean handle(ExchangeSession session, ExchangeRequestObject requestObject)

            throws HttpException {
               
               //The returned boolean is represents whether or not the request should be BLOCKED 
               true represents the blocked(and discards the request), or false represents transmits the request to peer
               return false;

            }
    }


    //Register RequestHandle instance to the Connector

    connector80.registerRequestHandle(owaLogonReq, handle);

    connector443.registerRequestHandle(owaLogonReq, handle);


    Testing again, because the password has been replaced, even the client inputs a right password he still got the "password
    error" message.


6. About SSLContext

    The SSLContext instance of your own organization is required. Here demonstrate it

    Supports there is a pkcs12 certificate file named my.p12 that contains the private key of Exchange server side,
    and the password of the certificate is "pass"

    
    KeyStore  = KeyStore.getInstance("PKCS12");

    ks.load(new ByteArrayInputStream(
        Files.readAllBytes(Paths.get("my.p12"))), "pass".toCharArray());

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

    kmf.init(ks, "pass".toCharArray());

    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

    tmf.init(ks);

    SSLContext.sslCtx = SSLContext.getInstance("TLSv1.2"); //TLSv1.2 is supported by JDK8

    sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    return sslCtx;


