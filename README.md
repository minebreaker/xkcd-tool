# xkcd-tool

CLI tool to view and search xkcd comics (unofficial)

```bash
# Opens a browser to show the comic
# `Desktop.getDesktop().browse()`を使う
$ xkcd interesting life
$ xkcd 308

# Shows a random comic
$ xkcd random
# Shows a latest comic
$ xkcd latest

# `--url` or `-l` flag to stdout the url of the comic
$ xkcd interesting life --url
https://xkcd.com/308/

# Using `--explain` or `-e` flags to open the relevant explain-xkcd page
$ xkcd interesting life --explain
$ xkcd interesting life --explain --url
# https://www.explainxkcd.com/wiki/index.php/308:_Interesting_Life
```

* xkcd-tool will cache indexes of comics in `~/.xkcd-tool` directory


# xkcd-tool-server

```
https://xkcd.aaload.com/interesting_life
https://xkcd.aaload.com/interesting-life
https://xkcd.aaload.com/random
https://xkcd.aaload.com/latest
https://xkcd.aaload.com/interesting-life?explain  # redirects to explain xkcd
```

* Use `Accept` header to change returned content:
    * defaults to `image/*` which returns raw image
        * Specific image type like `image/png` are just ignored
    * `text/html`
    * `application/json`  // http://xkcd.com/1234/info.0.json
    * or include `type=image` `type=html` in your query params

