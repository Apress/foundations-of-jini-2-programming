#!/usr/bin/perl
 
# This page could be an ordinary HTML page
# except that it needs to get the ServiceID
# from the URL request and pass this back in
# a hidden field

use CGI;

$query = new CGI;
print $query->header;
 
print $query->start_html(-title=>'File Classifier',
                                   -author=>'Jan Newmarch',
                                   -meta=>{'keywords'=>'',
                                           'copyright'=>'copyright 1996 Jan Newmarch'}
			 );
 
 
print "<h1 align=\"center\"> File Classifier </h1>\n";        

$serviceID = $query->param('serviceID'); 

# Form with a text field for filename and hidden field for serviceID
print $query->startform(-action=>'http://localhost/cgi-bin/FileClassifierRequest.sh');
print 'File name to classify';
print $query->textfield(-name=>'filename',
			-size=>20);
print $query->submit();
print $query->hidden(-name=>'serviceID',
		     -default=>[$serviceID]);
print $query->endform();

print $query->end_html;
  
