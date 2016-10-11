#!/usr/bin/perl -w

use strict;

my $sect_level = 0;
my $sect_1_count = 1;
my $sect_2_count = 1;


#############
# FUNCTIONS #
#############

# set this to false at the beginning of each file
# to knock out the chapter header
my $do_print = 0;

my $filename;
my $anchor;

while (<>) {
    if (/<?headings/) {
	$filename = $_;
	$filename =~ s/^.*<?headings \"//;
	$filename =~ s/\".*$//;
	chop $filename;
	
	# print "opening \"$filename\"\n";
	open(INPUT, $filename) or print "cant open $filename\n";
	
	#search for subheadings
        # set this to false at the beginning of each file
        # to knock out the chapter header
	$do_print = 0;

	print "  <orderedlist>\n";
	while (<INPUT>) {
	    if (/<sect1>/) {
		$do_print = 1;
	    } elsif (/<sect2>/) {
		$do_print = 0;
	    } elsif (/<\/sect2>/) {
		$do_print = 1;
	    } elsif (/<title id=/ && $do_print) {
		$_ =~ s/.*<title id=\"//;
		$anchor = $_;
		$anchor =~ s/\"?>.*//;
		chop $anchor;
		print "    <listitem> <ulink url=\"$filename#$anchor\"> $anchor </ulink> </listitem>\n";
		
	    }
	}
	close(INPUT);
	print "  </orderedlist>\n";
    } else {
	print "$_";
    }
}











