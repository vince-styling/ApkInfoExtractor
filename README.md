ApkInfoExtractor Introduction
=============================

## Introduction [here](http://apkinfoextractor.vincestyling.com/).

How to edit this site
=====================

I wrote the `generate.pl` script to parse the `content.md` as html then save to **index.html**, before you run that perl script,
make sure you already install `Text::MultiMarkdown` in the Perl Modules.

I recommend [cpanm](https://raw.github.com/miyagawa/cpanminus/master/cpanm) for installing that module :

    curl -o cpanm https://raw.github.com/miyagawa/cpanminus/master/cpanm
    chmod +x cpanm

    cpanm Text::MultiMarkdown
