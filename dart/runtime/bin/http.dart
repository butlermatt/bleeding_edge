// Copyright (c) 2012, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

/**
 * HTTP status codes.
 */
interface HttpStatus {
  static const int CONTINUE = 100;
  static const int SWITCHING_PROTOCOLS = 101;
  static const int OK = 200;
  static const int CREATED = 201;
  static const int ACCEPTED = 202;
  static const int NON_AUTHORITATIVE_INFORMATION = 203;
  static const int NO_CONTENT = 204;
  static const int RESET_CONTENT = 205;
  static const int PARTIAL_CONTENT = 206;
  static const int MULTIPLE_CHOICES = 300;
  static const int MOVED_PERMANENTLY = 301;
  static const int FOUND = 302;
  static const int MOVED_TEMPORARILY = 302; // Common alias for FOUND.
  static const int SEE_OTHER = 303;
  static const int NOT_MODIFIED = 304;
  static const int USE_PROXY = 305;
  static const int TEMPORARY_REDIRECT = 307;
  static const int BAD_REQUEST = 400;
  static const int UNAUTHORIZED = 401;
  static const int PAYMENT_REQUIRED = 402;
  static const int FORBIDDEN = 403;
  static const int NOT_FOUND = 404;
  static const int METHOD_NOT_ALLOWED = 405;
  static const int NOT_ACCEPTABLE = 406;
  static const int PROXY_AUTHENTICATION_REQUIRED = 407;
  static const int REQUEST_TIMEOUT = 408;
  static const int CONFLICT = 409;
  static const int GONE = 410;
  static const int LENGTH_REQUIRED = 411;
  static const int PRECONDITION_FAILED = 412;
  static const int REQUEST_ENTITY_TOO_LARGE = 413;
  static const int REQUEST_URI_TOO_LONG = 414;
  static const int UNSUPPORTED_MEDIA_TYPE = 415;
  static const int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
  static const int EXPECTATION_FAILED = 417;
  static const int INTERNAL_SERVER_ERROR = 500;
  static const int NOT_IMPLEMENTED = 501;
  static const int BAD_GATEWAY = 502;
  static const int SERVICE_UNAVAILABLE = 503;
  static const int GATEWAY_TIMEOUT = 504;
  static const int HTTP_VERSION_NOT_SUPPORTED = 505;
  // Client generated status code.
  static const int NETWORK_CONNECT_TIMEOUT_ERROR = 599;
}


/**
 * HTTP server.
 */
interface HttpServer default _HttpServer {
  HttpServer();

  /**
   * Start listening for HTTP requests on the specified [host] and
   * [port]. If a [port] of 0 is specified the server will choose an
   * ephemeral port. The optional argument [backlog] can be used to
   * specify the listen backlog for the underlying OS listen
   * setup. See [addRequestHandler] and [defaultRequestHandler] for
   * information on how incoming HTTP requests are handled.
   */
  void listen(String host, int port, [int backlog]);

  /**
   * Attach the HTTP server to an existing [:ServerSocket:]. If the
   * [HttpServer] is closed, the [HttpServer] will just detach itself,
   * and not close [serverSocket].
   */
  void listenOn(ServerSocket serverSocket);

  /**
   * Adds a request handler to the list of request handlers. The
   * function [matcher] is called with the request and must return
   * [:true:] if the [handler] should handle the request. The first
   * handler for which [matcher] returns [:true:] will be handed the
   * request.
   */
  addRequestHandler(bool matcher(HttpRequest request),
                    void handler(HttpRequest request, HttpResponse response));

  /**
   * Sets the default request handler. This request handler will be
   * called if none of the request handlers registered by
   * [addRequestHandler] matches the current request. If no default
   * request handler is set the server will just respond with status
   * code [:NOT_FOUND:] (404).
   */
  void set defaultRequestHandler(
      void handler(HttpRequest request, HttpResponse response));

  /**
   * Stop server listening.
   */
  void close();

  /**
   * Returns the port that the server is listening on. This can be
   * used to get the actual port used when a value of 0 for [port] is
   * specified in the [listen] call.
   */
  int get port;

  /**
   * Sets the error handler that is called when a connection error occurs.
   */
  void set onError(void callback(e));
}


/**
 * Access to the HTTP headers for requests and responses. In some
 * situations the headers will be imutable and the mutating methods
 * will then throw exceptions.
 *
 * For all operation on HTTP headers the header name is
 * case-insensitive.
 */
interface HttpHeaders default _HttpHeaders {
  static const ACCEPT = "Accept";
  static const ACCEPT_CHARSET = "Accept-Charset";
  static const ACCEPT_ENCODING = "Accept-Encoding";
  static const ACCEPT_LANGUAGE = "Accept-Language";
  static const ACCEPT_RANGES = "Accept-Ranges";
  static const AGE = "Age";
  static const ALLOW = "Allow";
  static const AUTHORIZATION = "Authorization";
  static const CACHE_CONTROL = "Cache-Control";
  static const CONNECTION = "Connection";
  static const CONTENT_ENCODING = "Content-Encoding";
  static const CONTENT_LANGUAGE = "Content-Language";
  static const CONTENT_LENGTH = "Content-Length";
  static const CONTENT_LOCATION = "Content-Location";
  static const CONTENT_MD5 = "Content-MD5";
  static const CONTENT_RANGE = "Content-Range";
  static const CONTENT_TYPE = "Content-Type";
  static const DATE = "Date";
  static const ETAG = "ETag";
  static const EXPECT = "Expect";
  static const EXPIRES = "Expires";
  static const FROM = "From";
  static const HOST = "Host";
  static const IF_MATCH = "If-Match";
  static const IF_MODIFIED_SINCE = "If-Modified-Since";
  static const IF_NONE_MATCH = "If-None-Match";
  static const IF_RANGE = "If-Range";
  static const IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
  static const LAST_MODIFIED = "Last-Modified";
  static const LOCATION = "Location";
  static const MAX_FORWARDS = "Max-Forwards";
  static const PRAGMA = "Pragma";
  static const PROXY_AUTHENTICATE = "Proxy-Authenticate";
  static const PROXY_AUTHORIZATION = "Proxy-Authorization";
  static const RANGE = "Range";
  static const REFERER = "Referer";
  static const RETRY_AFTER = "Retry-After";
  static const SERVER = "Server";
  static const TE = "TE";
  static const TRAILER = "Trailer";
  static const TRANSFER_ENCODING = "Transfer-Encoding";
  static const UPGRADE = "Upgrade";
  static const USER_AGENT = "User-Agent";
  static const VARY = "Vary";
  static const VIA = "Via";
  static const WARNING = "Warning";
  static const WWW_AUTHENTICATE = "WWW-Authenticate";

  static const GENERAL_HEADERS = const [CACHE_CONTROL,
                                        CONNECTION,
                                        DATE,
                                        PRAGMA,
                                        TRAILER,
                                        TRANSFER_ENCODING,
                                        UPGRADE,
                                        VIA,
                                        WARNING];

  static const ENTITY_HEADERS = const [ALLOW,
                                       CONTENT-ENCODING,
                                       CONTENT-LANGUAGE,
                                       CONTENT-LENGTH,
                                       CONTENT-LOCATION,
                                       CONTENT-MD5,
                                       CONTENT-RANGE,
                                       CONTENT-TYPE,
                                       EXPIRES,
                                       LAST-MODIFIED];


  static const RESPONSE_HEADERS = const [ACCEPT-RANGES,
                                         AGE,
                                         ETAG,
                                         LOCATION,
                                         PROXY-AUTHENTICATE,
                                         RETRY-AFTER,
                                         SERVER,
                                         VARY,
                                         WWW-AUTHENTICATE];

  static const REQUEST_HEADERS = const [ACCEPT,
                                        ACCEPT-CHARSET,
                                        ACCEPT-ENCODING,
                                        ACCEPT-LANGUAGE,
                                        AUTHORIZATION,
                                        EXPECT,
                                        FROM,
                                        HOST,
                                        IF-MATCH,
                                        IF-MODIFIED-SINCE,
                                        IF-NONE-MATCH,
                                        IF-RANGE,
                                        IF-UNMODIFIED-SINCE,
                                        MAX-FORWARDS,
                                        PROXY-AUTHORIZATION,
                                        RANGE,
                                        REFERER,
                                        TE,
                                        USER-AGENT];

  /**
   * Returns the list of values for the header named [name]. If there
   * is no headers with the provided name [:null:] will be returned.
   */
  List<String> operator[](String name);

  /**
   * Convenience method for the value for a single values header. If
   * there is no header with the provided name [:null:] will be
   * returned. If the header has more than one value an exception is
   * thrown.
   */
  String value(String name);

  /**
   * Adds a header value. The header named [name] will have the value
   * [value] added to its list of values. Some headers are single
   * values and for these adding a value will replace the previous
   * value. If the value is of type Date a HTTP date format will be
   * applied. If the value is a [:List:] each element of the list will
   * be added separately. For all other types the default [:toString:]
   * method will be used.
   */
  void add(String name, Object value);

  /**
   * Sets a header. The header named [name] will have all its values
   * cleared before the value [value] is added as its value.
   */
  void set(String name, Object value);

  /**
   * Removes a specific value for a header name. Some headers have
   * system supplied values and for these the system supplied values
   * will still be added to the collection of values for the header.
   */
  void remove(String name, Object value);

  /**
   * Remove all values for the specified header name. Some headers
   * have system supplied values and for these the system supplied
   * values will still be added to the collection of values for the
   * header.
   */
  void removeAll(String name);

  /**
   * Enumerate the headers applying the function [f] to each
   * header. The header name passed in [name] will be all lower
   * case.
   */
  void forEach(void f(String name, List<String> values));

  /**
   * Disable folding for the header named [name] when sending the HTTP
   * header. By default, multiple header values are folded into a
   * single header line by separating the values with commas. The
   * Set-Cookie header has folding disabled by default.
   */
  void noFolding(String name);

  /**
   * Gets and sets the date. The value of this property will
   * reflect the "Date" header.
   */
  Date date;

  /**
   * Gets and sets the expiry date. The value of this property will
   * reflect the "Expires" header.
   */
  Date expires;

  /**
   * Gets and sets the 'if-modified-since' date. The value of this property will
   * reflect the "if-modified-since" header.
   */
  Date ifModifiedSince;

  /**
   * Gets and sets the host part of the "Host" header for the
   * connection.
   */
  String host;

  /**
   * Gets and sets the port part of the "Host" header for the
   * connection.
   */
  int port;

  /**
   * Gets and sets the content type. Note that the content type in the
   * header will only be updated if this field is set
   * directly. Mutating the returned current value will have no
   * effect.
   */
  ContentType contentType;
}


/**
 * Representation of a header value in the form:
 *
 *   [:value; parameter1=value1; parameter2=value2:]
 *
 * [HeaderValue] can be used to conveniently build and parse header
 * values on this form.
 *
 * To build an [:Accepts:] header with the value
 *
 *     text/plain; q=0.3, text/html
 *
 * use code like this:
 *
 *     HttpClientRequest request = ...;
 *     var v = new HeaderValue();
 *     v.value = "text/plain";
 *     v.parameters["q"] = "0.3"
 *     request.headers.add(HttpHeaders.ACCEPT, v);
 *     request.headers.add(HttpHeaders.ACCEPT, "text/html");
 *
 * To parse the header values use the [:fromString:] constructor.
 *
 *     HttpRequest request = ...;
 *     List<String> values = request.headers[HttpHeaders.ACCEPT];
 *     values.forEach((value) {
 *       HeaderValue v = new HeaderValue.fromString(value);
 *       // Use v.value and v.parameters
 *     });
 */
interface HeaderValue default _HeaderValue {
  /**
   * Creates a new header value object setting the value part.
   */
  HeaderValue([String value]);

  /**
   * Creates a new header value object from parsing a header value
   * string with both value and optional parameters.
   */
  HeaderValue.fromString(String value);

  /**
   * Gets and sets the header value.
   */
  String value;

  /**
   * Gets the map of parameters.
   */
  Map<String, String> get parameters;

  /**
   * Returns the formatted string representation in the form:
   *
   *     value; parameter1=value1; parameter2=value2
   */
  String toString();
}


/**
 * Representation of a content type.
 */
interface ContentType extends HeaderValue default _ContentType {
  /**
   * Creates a new content type object setting the primary type and
   * sub type. If either is not passed their values will be the empty
   * string.
   */
  ContentType([String primaryType, String subType]);

  /**
   * Creates a new content type object from parsing a Content-Type
   * header value. As primary type, sub type and parameter names and
   * values are not case sensitive all these values will be converted
   * to lower case. Parsing this string
   *
   *     text/html; charset=utf-8
   *
   * will create a content type object with primary type [:text:], sub
   * type [:html:] and parameter [:charset:] with value [:utf-8:].
   */
  ContentType.fromString(String value);

  /**
   * Gets and sets the content type in the form "primaryType/subType".
   */
  String value;

  /**
   * Gets and sets the primary type.
   */
  String primaryType;

  /**
   * Gets and sets the sub type.
   */
  String subType;

  /**
   * Gets and sets the character set.
   */
  String charset;
}


/**
 * Representation of a cookie. For cookies received by the server as
 * Cookie header values only [:name:] and [:value:] fields will be
 * set. When building a cookie for the Set-Cookie header in the server
 * and when receiving cookies in the client as Set-Cookie headers all
 * fields can be used.
 */
interface Cookie default _Cookie {
  /**
   * Creates a new cookie optionally setting the name and value.
   */
  Cookie([String name, String value]);

  /**
   * Creates a new cookie by parsing a header value from a Set-Cookie
   * header.
   */
  Cookie.fromSetCookieValue(String value);

  /**
   * Gets and sets the name.
   */
  String name;

  /**
   * Gets and sets the value.
   */
  String value;

  /**
   * Gets and sets the expiry date.
   */
  Date expires;

  /**
   * Gets and sets the max age. A value of [:0:] means delete cookie
   * now.
   */
  int maxAge;

  /**
   * Gets and sets the domain.
   */
  String domain;

  /**
   * Gets and sets the path.
   */
  String path;

  /**
   * Gets and sets whether this cookie is secure.
   */
  bool secure;

  /**
   * Gets and sets whether this cookie is HTTP only.
   */
  bool httpOnly;

  /**
   * Returns the formatted string representation of the cookie. The
   * string representation can be used for for setting the Cookie or
   * Set-Cookie headers
   */
  String toString();
}


/**
 * Http request delivered to the HTTP server callback.
 */
interface HttpRequest default _HttpRequest {
  /**
   * Returns the content length of the request body. If the size of
   * the request body is not known in advance this -1.
   */
  int get contentLength;

  /**
   * Returns the persistent connection state signaled by the client.
   */
  bool get persistentConnection;

  /**
   * Returns the method for the request.
   */
  String get method;

  /**
   * Returns the URI for the request.
   */
  String get uri;

  /**
   * Returns the path part of the URI.
   */
  String get path;

  /**
   * Returns the query string.
   */
  String get queryString;

  /**
   * Returns the parsed query string.
   */
  Map<String, String> get queryParameters;

  /**
   * Returns the request headers.
   */
  HttpHeaders get headers;

  /**
   * Returns the cookies in the request (from the Cookie header).
   */
  List<Cookie> get cookies;

  /**
   * Returns the input stream for the request. This is used to read
   * the request data.
   */
  InputStream get inputStream;

  /**
   * Returns the HTTP protocol version used in the request. This will
   * be "1.0" or "1.1".
   */
  String get protocolVersion;

  /**
   * Get information about the client connection. Returns [null] if the socket
   * isn't available.
   */
  HttpConnectionInfo get connectionInfo;
}


/**
 * HTTP response to be send back to the client.
 */
interface HttpResponse default _HttpResponse {
  /**
   * Gets and sets the content length of the response. If the size of
   * the response is not known in advance set the content length to
   * -1 - which is also the default if not set.
   */
  int contentLength;

  /**
   * Gets and sets the status code. Any integer value is accepted, but
   * for the official HTTP status codes use the fields from
   * [HttpStatus].
   */
  int statusCode;

  /**
   * Gets and sets the reason phrase. If no reason phrase is explicitly
   * set a default reason phrase is provided.
   */
  String reasonPhrase;

  /**
   * Gets and sets the persistent connection state. The initial value
   * of this property is the persistent connection state from the
   * request.
   */
  bool persistentConnection;

  /**
   * Returns the response headers.
   */
  HttpHeaders get headers;

  /**
   * Cookies to set in the client (in the Set-Cookie header).
   */
  List<Cookie> get cookies;

  /**
   * Returns the output stream for the response. This is used to write
   * the response data. When all response data has been written close
   * the stream to indicate the end of the response.
   *
   * When this is accessed for the first time the response header is
   * send. Calling any methods that will change the header after
   * having retrieved the output stream will throw an exception.
   */
  OutputStream get outputStream;

  /**
   * Detach the underlying socket from the HTTP server. When the
   * socket is detached the HTTP server will no longer perform any
   * operations on it.
   *
   * This is normally used when a HTTP upgrade request is received
   * and the communication should continue with a different protocol.
   */
  DetachedSocket detachSocket();

  /**
   * Get information about the client connection. Returns [null] if the socket
   * isn't available.
   */
  HttpConnectionInfo get connectionInfo;
}


/**
 * HTTP client factory. The [HttpClient] handles all the sockets associated
 * with the [HttpClientConnection]s and when the endpoint supports it, it will
 * try to reuse opened sockets for several requests to support HTTP 1.1
 * persistent connections. This means that sockets will be kept open for some
 * time after a requests have completed, unless HTTP procedures indicate that it
 * must be closed as part of completing the request. Use [:HttpClient.shutdown:]
 * to force close the idle sockets.
 */
interface HttpClient default _HttpClient {
  static const int DEFAULT_HTTP_PORT = 80;

  HttpClient();

  /**
   * Opens a HTTP connection. The returned [HttpClientConnection] is
   * used to register callbacks for asynchronous events on the HTTP
   * connection. The "Host" header for the request will be set to the
   * value [host]:[port]. This can be overridden through the
   * HttpClientRequest interface before the request is sent. NOTE if
   * [host] is an IP address this will still be set in the "Host"
   * header.
   */
  HttpClientConnection open(String method, String host, int port, String path);

  /**
   * Opens a HTTP connection. The returned [HttpClientConnection] is
   * used to register callbacks for asynchronous events on the HTTP
   * connection. The "Host" header for the request will be set based
   * the host and port specified in [url]. This can be overridden
   * through the HttpClientRequest interface before the request is
   * sent. NOTE if the host is specified as an IP address this will
   * still be set in the "Host" header.
   */
  HttpClientConnection openUrl(String method, Uri url);

  /**
   * Opens a HTTP connection using the GET method. See [open] for
   * details. Using this method to open a HTTP connection will set the
   * content length to 0.
   */
  HttpClientConnection get(String host, int port, String path);

  /**
   * Opens a HTTP connection using the GET method. See [openUrl] for
   * details. Using this method to open a HTTP connection will set the
   * content length to 0.
   */
  HttpClientConnection getUrl(Uri url);

  /**
   * Opens a HTTP connection using the POST method. See [open] for details.
   */
  HttpClientConnection post(String host, int port, String path);

  /**
   * Opens a HTTP connection using the POST method. See [openUrl] for details.
   */
  HttpClientConnection postUrl(Uri url);

  /**
   * Shutdown the HTTP client releasing all resources.
   */
  void shutdown();
}


/**
 * A [HttpClientConnection] is returned by all [HttpClient] methods
 * that initiate a connection to an HTTP server. The handlers will be
 * called as the connection state progresses.
 *
 * The setting of all handlers is optional. If [onRequest] is not set
 * the request will be send without any additional headers and an
 * empty body. If [onResponse] is not set the response will be read
 * and discarded.
 */
interface HttpClientConnection {
  /**
   * Sets the handler that is called when the connection is established.
   */
  void set onRequest(void callback(HttpClientRequest request));

  /**
   * Sets callback to be called when the request has been send and
   * the response is ready for processing. The callback is called when
   * all headers of the response are received and data is ready to be
   * received.
   */
  void set onResponse(void callback(HttpClientResponse response));

  /**
   * Sets the handler that gets called if an error occurs while
   * connecting or processing the HTTP request.
   */
  void set onError(void callback(e));

  /**
   * Set this property to [:true:] if this connection should
   * automatically follow redirects. The default is [:true:].
   */
  bool followRedirects;

  /**
   * Set this property to the maximum number of redirects to follow
   * when [followRedirects] is [:true:]. If this number is exceeded the
   * [onError] callback will be called with a [RedirectLimitExceeded]
   * exception. The default value is 5.
   */
  int maxRedirects;

  /**
   * Returns the series of redirects this connection has been through.
   */
  List<RedirectInfo> get redirects;

  /**
   * Redirect this connection to a new URL. The default value for
   * [method] is the method for the current request. The default value
   * for [url] is the value of the [:HttpStatus.LOCATION:] header of
   * the current response. All body data must have been read from the
   * current response before calling [redirect].
   */
  void redirect([String method, Uri url]);

  /**
   * Detach the underlying socket from the HTTP client. When the
   * socket is detached the HTTP client will no longer perform any
   * operations on it.
   *
   * This is normally used when a HTTP upgrade is negotiated and the
   * communication should continue with a different protocol.
   */
  DetachedSocket detachSocket();

  /**
   * Get information about the client connection. Returns [null] if the socket
   * isn't available.
   */
  HttpConnectionInfo get connectionInfo;
}


/**
 * HTTP request for a client connection.
 */
interface HttpClientRequest default _HttpClientRequest {
  /**
   * Gets and sets the content length of the request. If the size of
   * the request is not known in advance set content length to -1,
   * which is also the default.
   */
  int contentLength;

  /**
   * Returns the request headers.
   */
  HttpHeaders get headers;

  /**
   * Cookies to present to the server (in the Cookie header).
   */
  List<Cookie> get cookies;

  /**
   * Gets and sets the requested persistent connection state.
   * The default value is [:true:].
   */
  bool persistentConnection;

  /**
   * Returns the output stream for the request. This is used to write
   * the request data. When all request data has been written close
   * the stream to indicate the end of the request.
   *
   * When this is accessed for the first time the request header is
   * send. Calling any methods that will change the header after
   * having retrieved the output stream will throw an exception.
   */
  OutputStream get outputStream;
}


/**
 * HTTP response for a client connection.
 */
interface HttpClientResponse default _HttpClientResponse {
  /**
   * Returns the status code.
   */
  int get statusCode;

  /**
   * Returns the reason phrase associated with the status code.
   */
  String get reasonPhrase;

  /**
   * Returns the content length of the request body. If the size of
   * the request body is not known in advance this -1.
   */
  int get contentLength;

  /**
   * Gets the persistent connection state returned by the server.
   */
  bool get persistentConnection;

  /**
   * Returns whether the status code is one of the normal redirect
   * codes [:HttpStatus.MOVED_PERMANENTLY:], [:HttpStatus.FOUND:],
   * [:HttpStatus.MOVED_TEMPORARILY:], [:HttpStatus.SEE_OTHER:] and
   * [:HttpStatus.TEMPORARY_REDIRECT:].
   */
  bool get isRedirect;

  /**
   * Returns the response headers.
   */
  HttpHeaders get headers;

  /**
   * Cookies set by the server (from the Set-Cookie header).
   */
  List<Cookie> get cookies;

  /**
   * Returns the input stream for the response. This is used to read
   * the response data.
   */
  InputStream get inputStream;
}

/**
 * Connection information.
 */
interface HttpConnectionInfo {
  String get remoteHost;
  int get remotePort;
  int get localPort;
}


/**
 * Redirect information.
 */
interface RedirectInfo {
  /**
   * Returns the status code used for the redirect.
   */
  int get statusCode;

  /**
   * Returns the method used for the redirect.
   */
  String get method;

  /**
   * Returns the location for the redirect.
   */
  Uri get location;
}


/**
 * When detaching a socket from either the [:HttpServer:] or the
 * [:HttpClient:] due to a HTTP connection upgrade there might be
 * unparsed data already read from the socket. This unparsed data
 * together with the detached socket is returned in an instance of
 * this class.
 */
interface DetachedSocket default _DetachedSocket {
  Socket get socket;
  List<int> get unparsedData;
}


class HttpException implements Exception {
  const HttpException([String this.message = ""]);
  String toString() => "HttpException: $message";
  final String message;
}


class RedirectException extends HttpException {
  const RedirectException(String message,
                          List<RedirectInfo> this.redirects) : super(message);
  final List<RedirectInfo> redirects;
}


class RedirectLimitExceededException extends RedirectException {
  const RedirectLimitExceededException(List<RedirectInfo> redirects)
      : super("Redirect limit exceeded", redirects);
}


class RedirectLoopException extends RedirectException {
  const RedirectLoopException(List<RedirectInfo> redirects)
      : super("Redirect loop detected", redirects);
}
