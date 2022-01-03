package de.openteilauto.openteilauto.api.teilauto

import android.content.Context
import android.text.TextUtils
import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.io.*

class SerializableCookie(private var cookie: Cookie) : Serializable {
    private val serialVersionUID = 2532101328282342578L

    fun getCookie(): Cookie {
        return cookie
    }

    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.domain())
        out.writeObject(cookie.expiresAt())
        out.writeObject(cookie.httpOnly())
        out.writeObject(cookie.name())
        out.writeObject(cookie.path())
        out.writeObject(cookie.secure())
        out.writeObject(cookie.value())
    }

    private fun readObject(inputStream: ObjectInputStream) {
        var cookieBuilder = Cookie.Builder()
            .domain(inputStream.readObject() as String)
            .expiresAt(inputStream.readObject() as Long)

        val httpOnly = inputStream.readObject() as Boolean
        if (httpOnly) {
            cookieBuilder = cookieBuilder.httpOnly()
        }

        cookieBuilder = cookieBuilder
            .name(inputStream.readObject() as String)
            .path(inputStream.readObject() as String)

        val secure = inputStream.readObject() as Boolean
        if (secure) {
            cookieBuilder = cookieBuilder.secure()
        }

        cookie = cookieBuilder
            .value(inputStream.readObject() as String)
            .build()
    }
}

class PersistentCookieJar(context: Context) : CookieJar {
    private val LOG_TAG = "PersistentCookieJar"

    private val COOKIE_PREF = "de.openteilauto.PREFERENCE_COOKIES"
    private val COOKIE_DOMAINS_STORE = "de.openteilauto.openteilauto.domain"
    private val COOKIE_DOMAIN_PREFIX = "de.openteilauto.openteilauto.domain_"
    private val COOKIE_NAME_PREFIX = "de.openteilauto.openteilauto.cookie_"

    private val cookiePrefs =
        context.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE)
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    init {
        val storedCookieDomains = cookiePrefs.getString(COOKIE_DOMAINS_STORE, null)
        if (storedCookieDomains != null) {
            val storedCookieDomainsArray = TextUtils.split(storedCookieDomains, ",")
            for (domain in storedCookieDomainsArray) {
                val storedCookiesNames = cookiePrefs.getString(COOKIE_DOMAIN_PREFIX + domain, null)

                if (storedCookiesNames != null) {
                    val storedCookiesNamesArray = TextUtils.split(storedCookiesNames, ",")
                    if (storedCookiesNamesArray != null) {
                        val cookies: MutableList<Cookie> = mutableListOf()
                        for (cookieName in storedCookiesNamesArray) {
                            val encCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + domain + cookieName, null)
                            if (encCookie != null) {
                                val decCookie = decodeCookie(encCookie)
                                if (decCookie != null) {
                                    cookies.add(decCookie)
                                }
                            }
                        }
                        cookieStore[domain] = cookies
                    }
                }
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        this.cookieStore[url.host()] = cookies

        // persist cookie
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(COOKIE_DOMAINS_STORE, TextUtils.join(",", cookieStore.keys))
        val names = mutableSetOf<String>()
        for (cookie in cookies) {
            names.add(cookie.name())
            prefsWriter.putString(COOKIE_NAME_PREFIX + url.host() + cookie.name(),
                encodeCookie(cookie))
        }
        prefsWriter.putString(COOKIE_DOMAIN_PREFIX + url.host(), TextUtils.join(",", names))
        prefsWriter.apply()
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookies = cookieStore[url.host()]
        return cookies?.toMutableList() ?: mutableListOf()
    }

    private fun encodeCookie(cookie: Cookie?): String? {
        if (cookie == null) {
            return null
        }

        val serializableCookie = SerializableCookie(cookie)

        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(serializableCookie)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }

        return byteArrayToHexString(os.toByteArray())
    }

    private fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)

        var cookie: SerializableCookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject()) as SerializableCookie
        } catch (e: IOException) {
            Log.e(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.e(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }

        return cookie?.getCookie()
    }

    private fun byteArrayToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private fun hexStringToByteArray(hexString: String): ByteArray {
        check(hexString.length % 2 == 0) {
            "Hex string must have an even length"
        }

        return hexString.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}