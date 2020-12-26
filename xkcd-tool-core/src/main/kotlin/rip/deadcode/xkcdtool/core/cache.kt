package rip.deadcode.xkcdtool.core

/*
 * xkcd-tool will cache indexes of comics in a `~/.xkcd-tool/index` file.
 *
 * The file is a TSV formatted for each line is a metadata of comics.
 *
 * Format: `id<tab>regularized form<tab>title<tab>image-url<tab>cached date (ISO)`
 * Example: `308<tab>interesting life<tab>Interesting Life<tab>https://img.xkcd.com/<tab>2020-01-01`
 */

/**
 * Raw data of the cache.
 *
 * @param id Comic ID aka the `num` field of the comic
 * @param regularizedTitle Title of the comic
 * @param title Original title
 * @param url Url of the image (not comic itself)
 */
data class CachedEntry(
    val id: Int,
    val regularizedTitle: String,
    val title: String,
    val url: String,
    val cachedDate: String
)
