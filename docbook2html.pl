#!/usr/local/bin/perl -w
# change the above to the location of the Perl binary on your system

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
use strict;
use XML::Parser;

# vbl to determine if we change href's to XML docs into HTML docs
# package name needed to allow it to be used in functions.pl
$main::hrefs_to_html = 1;
# stop the compiler complaining about nonuse of vbl
$main::hrefs_to_html = $main::hrefs_to_html;

$main::standalone = 1;
$main::standalone = $main::standalone;

do 'functions.pl';


# Exit the program unless there is exactly one command-line parameter.
# The one parameter should be an XML file.
die "Usage: xmlfaq.pl <file>" unless @ARGV == 1;

# a parser to quickly run through the file, fixing the X-refs
my $parser_table = new XML::Parser(Handlers => {Start => \&handle_table_start});             

# create a new instance of XML::Parser
# each time a new tag is discovered, it will call &handle_start
# each time an ending tag is found, it will call &handle_end
# when it finds a regular string, it will call &handle_char
                                                                   

my $parser = new XML::Parser(Handlers => {Start => \&handle_start,
                                          End   => \&handle_end,
			                  Char  => \&handle_char,
				          Doctype => \&handle_doc_type,
				          Final => \&handle_final,
				          ExternEnt => \&handle_extern_ent,
					  Proc => \&handle_proc});

my $sect_level = 0;
my $sect_1_count = 1;
my $sect_2_count = 1;

# parse the file whose name we specified as a command-line parameter

# 1st pass
$parser_table->parsefile($ARGV[0]);

# 2nd pass
$parser->parsefile($ARGV[0]);












