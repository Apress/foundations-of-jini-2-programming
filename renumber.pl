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

# The one parameter should be an XML file.
die "Usage: xmlfaq.pl <file>" unless @ARGV == 1;

# create a new instance of XML::Parser
# each time a new tag is discovered, it will call &handle_start
# each time an ending tag is found, it will call &handle_end
# when it finds a regular string, it will call &handle_char
my $parser_table = new XML::Parser(Handlers => {Start => \&handle_table_start});

my $parser_out = new XML::Parser(Handlers => {Start => \&handle_start,
					      End   => \&handle_end,
					      Char  => \&handle_char});

my $chap_no = 0;
my $figure_no = 1;
my $fig_id;
my $linkend;
my %table;

# parse the file whose name we specified as a command-line parameter
$parser_table->parsefile($ARGV[0]);

# now we have built up the table, redo the process
$parser_out->parsefile($ARGV[0]);


#############
# FUNCTIONS #
#############


my $k;
my $val;
foreach $k (keys(%table)) {
    $val = $table{$k};
    print $k, " + ", $val, "\n"; 
}
print "\n", %table;

#
# This builds the table of figure values
#
sub handle_table_start {
    my $p = shift;                       # This is the reference to the parser object.
                                         # We don't use it directly, but I thought
                                         # you'd like to know anyway.
    my $el = shift;                      # ooh! This is the name of the element the
                                         # parser found.

    my %attribs = @_;                    # Everything else that's passed are name/value
                                         # pairs. Since a hash is really an array, we can
                                         # pull them into a hash. neat!

    # Here is where we buld the table from chapter id and figure id's
    if ($el =~ /\bchapter\b/i) {
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\bid\b/i) {
		$chap_no = $attribs{$attrib};
	    }
	}
    } elsif ($el =~ /\bfigure\b/i) {
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\bid\b/i) {
		$fig_id = $attribs{$attrib};
		# add the fig_id + figure_no to a table
		$table{$fig_id} = "$chap_no" . "." . "$figure_no";
		$figure_no++;
	    }
	}
    }
}

#
# These handle the second parse and produce output
#

sub handle_start {
    my $p = shift;                       # This is the reference to the parser object.
                                         # We don't use it directly, but I thought
                                         # you'd like to know anyway.
    my $el = shift;                      # ooh! This is the name of the element the
                                         # parser found.

    my %attribs = @_;                    # Everything else that's passed are name/value
                                         # pairs. Since a hash is really an array, we can
                                         # pull them into a hash. neat!

    # Here is where we substitute the cross references
    if ($el =~ /\bxref\b/i) {
	print "<xref ";
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\bid\b/i) {
		# ignore any pre-existing id value
		next;
	    }
	    print " $attrib = \"$attribs{$attrib}\"";
	    if ($attrib =~ /\blinkend\b/i) {
		$linkend = $attribs{$attrib};
		# extract the fig_no using linkend from a table
		if (defined($table{$linkend})) {
		    print " id = \"", $table{$linkend}, "\"";
		}
	    }
	}
	print ">";
    } else {                             # for all unrecognized tags, we simply print them.
                                         # I did this so I can easily include HTML tags.
                                         # One problem is that it doesn't print the attributes
	                                 # in order, but that's ok here.
	print "<$el";
	foreach my $attrib (keys(%attribs)) {
	    print " $attrib = \"$attribs{$attrib}\"";
	}
	print ">";
    }
}

# here we handle all non-tag strings. All we need to do here is print whatever is passed
sub handle_char {
    my ($p, $data) = @_;
    my $tag = $p -> current_element;
    print $data;
}

# here we handle ending tags.
sub handle_end {
    my $p = shift;
    my $el = shift;
    my %atrribs = @_;

    if ($el =~ /\bfaq\b/i) {
	print "</html>";
    } else {
	print "</$el>";
    }
}
