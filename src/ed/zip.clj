(ns ed.zip
  (:gen-class)
  (:import (java.util.zip InflaterInputStream DeflaterInputStream ZipException Inflater)
           (org.apache.commons.io IOUtils)
           (java.io InputStream BufferedInputStream ByteArrayInputStream)))

(defn utf8-bytes
  "Returns the encoding's bytes corresponding to the given string. If no
  encoding is specified, UTF-8 is used."
  [^String s & [^String encoding]]
  (.getBytes s (or encoding "UTF-8")))

(defn utf8-string
  "Returns the String corresponding to the given encoding's decoding of the
  given bytes. If no encoding is specified, UTF-8 is used."
  [^"[B" b & [^String encoding]]
  (String. b (or encoding "UTF-8")))

(defn inflate
  "Returns a zlib inflate'd version of the given byte array or InputStream."
  [b]
  (when b
    ;; This weirdness is because HTTP servers lie about what kind of deflation
    ;; they're using, so we try one way, then if that doesn't work, reset and
    ;; try the other way
    (let [stream (BufferedInputStream. (if (instance? InputStream b)
                                         b
                                         (ByteArrayInputStream. b)))
          _ (.mark stream 512)
          iis (InflaterInputStream. stream)
          readable? (try (.read iis) true
                         (catch ZipException _ false))]
      (.reset stream)
      (if readable?
        (InflaterInputStream. stream)
        (InflaterInputStream. stream (Inflater. true))))))

(defn deflate
  "Returns a deflated version of the given byte array."
  [b]
  (when b
    (IOUtils/toByteArray (DeflaterInputStream. (ByteArrayInputStream. b)))))