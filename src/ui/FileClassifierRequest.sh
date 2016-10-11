#!/bin/sh

# A CGI script called in response to Submit on a
# a FileClassifier Web UI page. This just calls
# into a Jini script that locates and calls on 
# the FileClassifier service

JAVA_HOME=/usr/local/src/jdk1.2.2
HTML_HOME=/home/WWW/htdocs
CODEBASE=classes
CGI_HOME=/home/jan/projects/jini/doc
POLICY_HOME=/home/jan/projects/jini/doc
JINI_HOME=/home/jan/tmpdir/jini1_1

CLASSPATH=$HTML_HOME/$CODEBASE:$JINI_HOME/lib/jini-core.jar:$JINI_HOME/lib/jini-ext.jar:$JINI_HOME/lib/sun-util.jar:$CGI_HOME:$CLASSPATH
export CLASSPATH

echo Content-type: text/html
echo  

$JAVA_HOME/bin/java \
  -DREQUEST_METHOD="$REQUEST_METHOD" \
  -DQUERY_STRING="$QUERY_STRING" \
  -Djava.security.policy=$POLICY_HOME/policy.all \
  ui.FileClassifierRequest


