package ilhoyu

import org.apache.http.impl.client.HttpClientBuilder
import org.jsoup.Jsoup
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

open class SpamLinkChecker {

    private val urlRegex = "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])".toRegex()

    private fun String.isUrl() = urlRegex.matches(this)

    private val restTemplate: RestTemplate by lazy { restTemplate() }

    protected fun restTemplate(): RestTemplate {
        return RestTemplate().apply {
            requestFactory = HttpComponentsClientHttpRequestFactory().apply {
                httpClient = HttpClientBuilder.create()
                    .disableRedirectHandling()
                    .build()
            }
        }
    }

    fun extractLinksFrom(content: String, dupCheck: Set<String>): Set<String> {
        return mutableSetOf<String>().also { result ->
            val doc = Jsoup.parse(content)

            doc.getElementsByTag("a").forEach {
                if (it.hasAttr("href")) {
                    it.attr("href").also { mayUrl ->
                        if (mayUrl.isUrl() && !dupCheck.contains(mayUrl)) {
                            result.add(mayUrl)
                        }
                    }
                }
            }

            urlRegex.findAll(doc.text()).forEach { mr ->
                    result.add(mr.groupValues[1])
                }
        }
    }

    fun extractLinksFrom(links: Set<String>, dupCheck: Set<String>): Set<String> {
        return mutableSetOf<String>().also { result ->
            links.forEach { url ->
                val response = restTemplate.getForEntity(url, String::class.java)
                val redirection = response.headers[HttpHeaders.LOCATION]?.last()

                redirection?.also { mayUrl ->
                    if (mayUrl.isUrl() && !dupCheck.contains(mayUrl)) {
                        result.add(mayUrl)
                    }
                } ?: result.addAll(extractLinksFrom(response.body, dupCheck))
            }
        }
    }

    fun findSubLinksFrom(content: String, dupCheck: Set<String>, redirectionDepth: Int): Set<String> {
        if (redirectionDepth == 0) return setOf()

        val subLinks = extractLinksFrom(content, dupCheck).toMutableSet()

        subLinks.addAll(findSubLinksFrom(subLinks, dupCheck + subLinks, redirectionDepth.dec()))

        return subLinks
    }

    fun findSubLinksFrom(links: Set<String>, dupCheck: Set<String>, redirectionDepth: Int): Set<String> {
        if (redirectionDepth == 0) return setOf()

        val subLinks = extractLinksFrom(links, dupCheck).toMutableSet()

        subLinks.addAll(findSubLinksFrom(subLinks, dupCheck + subLinks, redirectionDepth.dec()))
        subLinks.addAll(links)

        return subLinks
    }

    fun isSpam(content: String, spamLinkDomains: List<String>, redirectionDepth: Int): Boolean {
        val links = findSubLinksFrom(content, setOf(), redirectionDepth + 1)

        if (links.isEmpty()) return false

        for (domain in spamLinkDomains) {
            for (url in links) {
                if (url.contains(domain)) return true
            }
        }

        return false
    }

}