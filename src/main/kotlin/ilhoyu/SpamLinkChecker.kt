package ilhoyu

import org.jsoup.Jsoup

object SpamLinkChecker {

    fun extractLinks(content: String): Set<String> {
        return mutableSetOf<String>().also { result ->
            val doc = Jsoup.parse(content)

            doc.getElementsByTag("a").forEach {
                if (it.hasAttr("href")) result.add(it.attr("href"))
            }

            "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])"
                .toRegex().findAll(doc.text()).forEach { mr ->
                    result.add(mr.groupValues[1])
                }
        }
    }

    fun isSpam(content: String, spamLinkDomains: List<String>, redirectionDepth: Int): Boolean {
        val links = extractLinks(content)

        // TODO Gathering sub-links from `links` until redirectionDepth to 0

        if (links.isEmpty()) return false

        for (v in spamLinkDomains) {
            if (links.contains(v)) return true
        }

        return false
    }

}