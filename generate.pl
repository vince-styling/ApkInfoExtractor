#!/usr/bin/perl -w

use File::Slurp;
use Text::MultiMarkdown;

my $markdownInstance = Text::MultiMarkdown->new(
    empty_element_suffix => '>',
    tab_width => 4,
    use_wikilinks => 0,
) if !$@;

# read the content.md content.
my $contentFile = "$ENV{PWD}/content.md";
my $content = read_file($contentFile, binmode => ':utf8');

# parse the content as html content.
my $parsedContent = $markdownInstance ? $markdownInstance->markdown($content) : $content;

# read the index.html.
my $indexFile = "$ENV{PWD}/index.html";
my $indexContent = read_file($indexFile, binmode => ':utf8');

# search the start string of index.html.
my $startStr = '<section id="main_content">';
my $startIndex = index($indexContent, $startStr);

# search the end string of index.html.
my $endIndex = index($indexContent, '</section>', $startIndex);

my $head = substr($indexContent, 0, $startIndex);
my $tail = substr($indexContent, $endIndex);

my $finalContent = $head . $startStr . $parsedContent . $tail;

# write back the content to index.html file.
open my $fh, ">:utf8", $indexFile;
binmode($fh, ":utf8");
print $fh $finalContent;
close $fh;

print "Done!\n";