package info.lindblad.prometheus.cloudwatch.proxy.util

import java.math.BigInteger

object Hash {

  import java.security.MessageDigest

  val digest = MessageDigest.getInstance("SHA")

  def sha1(s: String) = {
    digest.digest(s.getBytes).map("%02x".format(_)).mkString
  }

}