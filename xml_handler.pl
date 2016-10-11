#!/usr/local/bin/perl

use strict;
use CGI;
use XML::Parser;
do 'functions.pl';

my $query = new CGI;

print $query->header('text/html', '200 OK');
#print "query: ", $query->query_string;
#print "\nkeywords: ", $query->keywords;
#print "\ndump: ", $query->dump;
#print "\npath-info: ", $query->path_info();
#print "\npwd: ", `pwd`;
#print "\npath trabslated: ", $query->path_translated();



#*********************************************************************#
# xmlfaq.pl Copyright 1998 Jonathan Eisenzopf, All rights reserved    #
# AUTHOR: Jonathan Eisenzopf <eisen@pobox.com>                        #
# DESCRIPTION: A short Perl script that parses my Perl XML FAQ and    #
# converts it into HTML.                                              #
#*********************************************************************#
# DISCLAIMER                                                          #
# Feel free to abuse this code for your personal pleasure and gain.   #
# Of course, don't blame me if it doesn't work or destroys things.    #
#*********************************************************************#

########
# MAIN #
########

# a parser to quickly run through the file, fixing the X-refs
my $parser_table = new XML::Parser(Handlers => {Start => \&handle_table_start});             

#
# create a new instance of XML::Parser
# each time a new tag is discovered, it will call &handle_start
# each time an ending tag is found, it will call &handle_end
# when it finds a regular string, it will call &handle_char
#print "\ncreating parser\n";
my $parser = new XML::Parser(Handlers => {Start => \&handle_start,
                                          End   => \&handle_end,
			                  Char  => \&handle_char,
				          Doctype => \&handle_doc_type,
				          Final => \&handle_final,
				          ExternEnt => \&handle_extern_ent,
					  Proc => \&handle_proc},
			     Style => 'Debug');

my $sect_level = 0;
my $sect_1_count = 1;
my $sect_2_count = 1;

# parse the file whose name we specified as a command-line parameter
my $path_translated = $query->path_translated();
my $dir = $path_translated;
# lose from last /
$dir =~ s#/\w*.xml##;
#print "\ndir: ", $dir;

my $path = $path_translated;
# lose upto last /
$path =~ s#^.*/##;

# vbl to determine if we change href's to XML docs into HTML docs
# package name needed to allow it to be used in functions.pl
$main::hrefs_to_html = 0;
# stop the compiler complaining about nonuse of vbl
$main::hrefs_to_html = $main::hrefs_to_html;

# do the same in general
$main::standalone = 0;
$main::standalone = $main::standalone;


#print "\nparser created\n, $path";
chdir($dir);


# 1st pass
$parser_table->parsefile($path);

# 2nd pass
$parser->parsefile($path);

#print "\nparser parsed\n";










