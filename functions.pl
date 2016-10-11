
#############
# FUNCTIONS #
#############

my $chap_no = 0;
my $chap_title = "";
my $figure_no = 1;

# for the first pass to work out Figure numbers in X-refs
my $table_figure_no = 1;
my $fig_id;
my $linkend;
my %table;

my $sect_level = 0;
my $sect_1_count = 0;
my $sect_2_count = 0;
my $sect_3_count = 0;
my $do_print = 1;

my $toc_level = 0;
my $toc_1_count = 0;
my $toc_2_count = 0;

# suppress printing sometimes, like toc in HTML standalone
my $suppress_printing = 0;

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
		$table{$fig_id} = "$chap_no" . "." . "$table_figure_no";
		$table_figure_no++;
	    }
	}
    }
}

# 
# These perform the 2nd pass, and produce output
#
sub handle_start {

    # suppress printing is turned on by a start tag (tocchap)
    # and off by an end tag. Any changes to that and we will
    # need to move this code into each tag
    if ($suppress_printing) {
	return;
    }

    my $p = shift;                       # This is the reference to the parser object.
                                         # We don't use it directly, but I thought
                                         # you'd like to know anyway.
    my $el = shift;                      # ooh! This is the name of the element the
                                         # parser found.

    my %attribs = @_;                    # Everything else that's passed are name/value
                                         # pairs. Since a hash is really an array, we can
                                         # pull them into a hash. neat!

    # Here is where we start identifying each tag, and printing something.
    if ($el =~ /\bfaq\b/i) {             # <faq> is the root tag, so we replace it with 
                                         # <html>
	print "<html>";
    } elsif ($el =~ /\bbook\b/i) {
	print "<head>\n";
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\btitle\b/i) {
		$title = $attribs{$attrib};
	    } elsif ($attrib =~ /\bid\b/i) {
		$chap_no = $attribs{$attrib};
	    }
	}
	print "<title>\n$title\n</title>\n";
	print "</head>\n<body>\n";

    } elsif ($el =~ /\bchapter\b/i) {     # the <chapter> tag
	$sect_level = 0;
	$sect_1_count = 0;
	# we should only do the next stuff if we are sure
	# we are standalone and not part of a book
	print "<head>\n";
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\btitle\b/i) {
		$chap_title = $attribs{$attrib};
	    } elsif ($attrib =~ /\bid\b/i) {
		$chap_no = $attribs{$attrib};
	    }
	}
	print "<title>\nChapter $chap_no: $chap_title\n</title>\n";
	print "</head>\n<body>\n";

    } elsif ($el =~ /\babstract\b/i) {  # the <abstract> tag
	print "<blockquote>\n<em>\n";
    } elsif ($el =~ /\bsect1\b/i) {     # the <sect1> tag
	$sect_level = 1;
	$sect_2_count = 0;
    } elsif ($el =~ /\bsect2\b/i) {     # the <sect2> tag
	$sect_level = 2;
	$sect_3_count = 0;
    } elsif ($el =~ /\bsect3\b/i) {     # the <sect3> tag
	$sect_level = 3;
    } elsif ($el =~ /\btitle\b/i) {      # the <title> tag
	print "<a";
	if ($p->current_element =~ /\bfigure\b/i) {
	    # we're a figure, not a section header
	    print ">\n <center><em>Figure $chap_no.$figure_no:</em> ";
	    $figure_no++;
	    return;
	}

	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\bname\b/i) {
		print " name = \"$attribs{$attrib}\"";
	    }
	}
	print ">\n";

	# kludge to set chapter_no to real text, unless it is zero,
	# which happens in tut contents
	local $chap_header;
	if ($chap_no == 0) {
	    $chap_header = "";
	} else {
	    $chap_header = $chap_no . ".";
	}

	if ($sect_level == 0) {
	    print "<h1 align=\"center\">";
	    if ($p->current_element =~ /\bchapter\b/i) {
		print "Chapter $chap_no: ";
	    }
	} elsif ($sect_level == 1) {
	    $sect_1_count++;
	    # print "<a name=\"section_$sect_1_count\">\n";
	    print "<h2> ", $chap_header, $sect_1_count, ". ";
	} elsif ($sect_level == 2) {
	    $sect_2_count++;
	    # print "<a name=\"subsection_$sect_1_count.$sect_2_count\">\n";
	    print "<h3> ", $chap_header, "$sect_1_count.$sect_2_count ";
	} elsif ($sect_level == 3) {
	    $sect_3_count++;
	    # print "<a name=\"subsection_$sect_1_count.$sect_2_count.$sect_3_count\">\n";
	    print "<h4> ", $chap_header, "$sect_1_count.$sect_2_count.$sect_3_count ";
	}

    ## Table of contents stuff
    } elsif ($el =~ /\btocchap\b/i) {     # the <tocchap> tag
	if ($standalone) {
	    $suppress_printing = 1;
	    return;
	}
	print "<blockquote>\n";
    } elsif ($el =~ /\btoclevel1\b/i) {     # the <toclevel1> tag
        # use HTML list rather our own
	print "<ul>\n";
    } elsif ($el =~ /\btoclevel2\b/i) {     # the <toclevel2> tag
        # use HTML list rather our own
	print "<ul>\n";
    } elsif ($el =~ /\btocentry\b/i) {      # the <tocentry> tag
	print "<li>";

    ## info stuff
    } elsif ($el =~ /\bauthor\b/i) {     # the <author> tag
	print "<h2 align=\"center\">";
    } elsif ($el =~ /\brevnumber\b/i) {    # the <version number> tag
	print "<h3 align=\"center\">Version ";
    } elsif ($el =~ /\bdate\b/i) {    # the date revised tag
	print "<h3 align=\"center\">";

    ## other stuff
    } elsif ($el =~ /\bpara\b/i) {  # the <para> tag
	print "<p>";

    ## figures
    } elsif ($el =~ /\bfigure\b/i) {  # the <figure> tag
	# nothing - just a place holder for title tag at this level
    } elsif ($el =~ /\bgraphic\b/i) {  # the <graphic> tag
	print "<center>\n<img ";
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\bfileref\b/i) {
		print " src = \"$attribs{$attrib}\"";
	    } else {
		print " $attrib = \"$attribs{$attrib}\"";
	    }
	}
	print ">\n</center>";

    } elsif ($el =~ /\bprogramlisting\b/i) {  # the <program listing> tag
	print "<pre><code>";
    } elsif ($el =~ /\bfuncsynopsis\b/i) {  # the <funcsynopsis> tag
	print "<pre><code>";
    } elsif ($el =~ /\bmodified\b/i) {  # last modified tag
	print "(Last modified: ";
    } elsif ($el =~ /\bitemizedlist\b/i) {
	print "<ul>\n";
    } elsif ($el =~ /\borderedlist\b/i) {
	print "<ol>\n";
    } elsif ($el =~ /\blistitem\b/i) {
	print "<li>";
    } elsif ($el =~ /\bemphasis\b/i) {
	print "<em>";
    } elsif ($el =~ /\bulink\b/i) {
	print "<a ";
	foreach my $attrib (keys(%attribs)) {
	    if ($attrib =~ /\burl\b/i) {
		$href = $attribs{$attrib};
 		if ($main::hrefs_to_html) {
#		    # print "hrefs to html defined";
		    my $url = $attribs{$attrib};
		    $url =~ s/xml/html/;
		    print " href = \"$url\"";
		} else {
#		    print "hrefs to html not defined";		    
		    # print " href = \"$attribs{$attrib}\"";
		}
	    } else {
		print " $attrib = \"$attribs{$attrib}\"";
	    }
	}
	print ">";
    } elsif ($el =~ /\bxref\b/i) {     # cross references for figure numbers
	# print "<xref ";
	foreach my $attrib (keys(%attribs)) {
	    # print " $attrib = \"$attribs{$attrib}\"";
	    if ($attrib =~ /\blinkend\b/i) {
		$linkend = $attribs{$attrib};
		# extract the fig_no using linkend from a table
		if (defined($table{$linkend})) {
		    print $table{$linkend};
		}
	    }
	}
	# print ">";
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
    if ($suppress_printing) {
	return;
    }

    my ($p, $data) = @_;
    my $tag = $p -> current_element;
    # The headings stuff later needs to have all stuff in section title
    # on one line, so we stuff around here in order to do that - gross
    #if ($tag =~ /\btitle\b/) {
	#print "Title tag\n";
	#if ($data eq "\n") {
	    # print "empty\n";
	#} else {
	 #   print $data;
	#}
    #} else {
	#print $data;
    #}
    print $data;
}

# here we handle ending tags.
sub handle_end {
    my $p = shift;
    my $el = shift;
    my %atrribs = @_;

    if ($suppress_printing) {
	if ($el =~ /\btocchap\b/i) {
	    $suppress_printing = 0;
	}
	return;
    }

    if ($el =~ /\bfaq\b/i) {
	print "</html>";
    } elsif ($el =~ /\bbook\b/i) {

    } elsif ($el =~ /\btitle\b/i) {     # the <title> tag
	if ($p->current_element =~ /\bfigure\b/i) {
	    # we're a figure, not a section header
	    print "</center></a>";
	    return;
	}

	if ($sect_level == 0) {
	    print "</h1>";
	} elsif ($sect_level == 1) {
	    print "</h2>\n";
	    # print "</a>\n";
	} elsif ($sect_level == 2) {
	    print "</h3>\n";
	    # print "</a>\n";
	} elsif ($sect_level == 3) {
	    print "</h4>\n";
	    # print "</a>\n";
	}
	print "</a>";
    } elsif ($el =~ /\btocchap\b/i) {     # the <tocchap> tag
	if ($standalone) {
	    $suppress_printing = 0;
	    return;
	}

	print "</blockquote>\n";
    } elsif ($el =~ /\btocentry\b/i) {      # the <tocentry> tag
	print "</li>";
    } elsif ($el =~ /\btoclevel1\b/i) {     # the <toclevel1> tag
	print "</ul>\n";
    } elsif ($el =~ /\btoclevel2\b/i) {     # the <toclevel2> tag
	print "</ul>\n";
    } elsif ($el =~ /\bauthor\b/i) {        # the <author> tag
	print "</h2>\n";
    } elsif ($el =~ /\brevnumber\b/i) {        # the <revnumber> tag
	print "</h3>\n";
    } elsif ($el =~ /\bdate\b/i) {        # the <date> tag
	print "</h3>\n";
     } elsif ($el =~ /\bpara\b/i) {        # the <para> tag
	print "</p>\n";
    } elsif ($el =~ /\bprogramlisting\b/i) {  # the <programlisting> tag
	print "</code></pre>\n";
    } elsif ($el =~ /\bfuncsynopsis\b/i) {  # the <funcsynopsis> tag
	print "</code></pre>\n";
    } elsif ($el =~ /\bmodified\b/i) {
	print ")";
     } elsif ($el =~ /\bitemizedlist\b/i) {  # the <itemizedlist> tag
	print "</ul>\n";
     } elsif ($el =~ /\borderedlist\b/i) {  # the <orderelist> tag
	print "</ol>\n";
     } elsif ($el =~ /\blistitem\b/i) {  # the <listitem> tag
	print "</li>\n";
    } elsif ($el =~ /\bemphasis\b/i) {  # the <emphasis> tag
	print "</em>";
    } elsif ($el =~ /\bsect1\b/i) {         # the <sect1> tag

    } elsif ($el =~ /\bsect2\b/i) {         # the <sect2> tag

    } elsif ($el =~ /\bsect3\b/i) {         # the <sect3> tag

    } elsif ($el =~ /\bchapter\b/i) {        # the <chapter> tag

    } elsif ($el =~ /\babstract\b/i) {        # the <abstract> tag
	print "</em>\n</blockquote>\n";
    } elsif ($el =~ /\bulink\b/i) {        # the <ulink> tag
	print "</a>";
    } elsif ($el =~ /\bfigure\b/i) {        # the <figure> tag
	# empty
    } elsif ($el =~ /\bgraphic\b/i) {        # the <graphic> tag
	# empty
    } else {
	print "</$el>";
    }
}

# here we handle document type.
sub handle_doc_type {
    my $p = shift;
    my $name = shift;

    print "<html>\n";
#<head>\n\
#</head>\n\
#<body>\n";
}

sub handle_final {
    print "</body>\n</html>\n";
}

sub handle_extern_ent {
    my  ($p, $base, $sysid, $pubid) = @_;
    open(INPUT, $sysid) or return "can't open external file $sysid";
    my $extern = "";
    while (<INPUT>) {
	$extern .= $_;
    }
    close INPUT;
    return $extern;
}

sub handle_proc {
    my ($p, $target, $data) = @_;
    my $anchor;
    my $heading;
    my $in_name = "";
    my $line_no = 1;
    my $header_comment = 0;

    $data =~ s/\"//g;
    # print "opening $data\n";
    open(INPUT, $data) or print "cant open $data\n";
    if ($target =~ /program/) {
	LINE: while (<INPUT>) {
	    # print the 1st two lines and the last line of
	    # any doc type headers
	    if (m/^ \*\//) {
		$header_comment = 0;
	    }
	    if (m/^\/\*\*/) {
		$header_comment = 1;
	    }
	    if ($header_comment) {
		$header_comment++;
	    }
	    next LINE if ($header_comment > 3);

	    # sanitize awkward characters
	    $_ =~ s/\&/\&amp;/g;
	    $_ =~ s/</\&lt;/g;
	    $_ =~ s/>/\&gt;/g;

	    # print with line number $.
	    # print $., "\t$_";
	    print $_;
	} # LINE
    }
    close(INPUT);
}
