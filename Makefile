
# Your Java compiler
JAVAC = javac

# Compile flags
JFLAGS = -g

# Where your Jini distribution was unpacked
# JINI_HOME = /usr/local/jini1_1
JINI_HOME = /usr/local/jini2_0

# Where your HTTP server delivers its web pages from
HTML_HOME = /home/httpd/html

# The subdirectory that is used as codebase in HTML_HOME
CODEBASE = classes

# Where to install the client-only programs so you can 
# run the clients without picking up class files from the
# main build directory
CLIENT_HOME = /home/newmarch/tmpdir/testjini

# Where the Service UI files are
SERVICEUI = /home/httpd/html/java/jini/uiapi100.zip

# BEA Weblogic. If you don't have Weblogic, set these to empty
#WEBLOGIC=/usr/local/weblogic/classes:/usr/local/weblogic/lib/weblogicaux.jar
#WEBLOGIC_CLASSES = \
#	ejb/FileClassifier.class \
#	ejb/FileClassifierHome.class \
#	ejb/FileClassifierBean.class \
#	ejb/FileClassifierClient.class \
#	ejb/FileClassifierImpl.class \
#	ejb/FileClassifierServer.class \
#	ejb/META-INF/ejb-jar.xml
WEBLOGIC=
WEBLOGIC_CLASSES =

TOMCAT_HOME = /usr/local/jakarta-tomcat
SERVLET = $(TOMCAT_HOME)/lib/servlet.jar
SERVLET_CLASS_PATH = $(TOMCAT_HOME)/webapps/jini/WEB-INF/classes/
SERVLET_COMMON_CLASS_PATH = $(TOMCAT_HOME)/classes/

# RCX support. If you don't have the RCX files, set these to empty
# Where the RCX class files are
RCX_PKG = /usr/local/src/rcx.zip
# The RCX+Jini files
RCX_CLASSES = \
	rcx/jini/RCXPortInterface.class \
	rcx/jini/RemoteRCXPort.class \
	rcx/jini/RCXPortImpl.class \
	rcx/jini/RCXPortProxy.class \
	rcx/jini/RCXServer.class \
	rcx/jini/JiniRCXPort.class \
	rcx/jini/CompileException.class \
	rcx/jini/NotQuiteC.class \
	rcx/jini/RemoteNotQuiteC.class \
	rcx/jini/NotQuiteCImpl.class \
	client/TestRCX.class \
	rcx/jini/RCXLoaderFrame.class \
	rcx/jini/RCXLoaderFrameFactory.class \
	rcx/jini/RCXServer2.class \
	rcx/jini/CarJFrame.class \
	rcx/jini/CarJFrameFactory.class \
	client/TestRCX2.class \
	client/TestRCX3.class

# CGI classes from Marty Hall
CGI_CLASSES = \
	cgi/StringVector.class \
	cgi/CgiParser.class \
	cgi/LookupTable.class \
	cgi/QueryStringParser.class \
	cgi/URLDecoder.class

##########################################
# No changes should be needed below here #
##########################################

CLASSPATH = .:../sap:$(JINI_HOME)/lib/reggie.jar:$(JINI_HOME)/lib/jini-core.jar:$(JINI_HOME)/lib/jini-ext.jar:$(JINI_HOME)/lib/sun-util.jar:$(RCX_PKG):$(SERVICEUI):$(WEBLOGIC):$(SERVLET)

%.class: %.java
	$(JAVAC) $(JFLAGS) -deprecation -classpath $(CLASSPATH)  $<

%.html: %.xml docbook2html.pl functions.pl
	perl docbook2html.pl $< > $@

RMIC_ARGS = -v1.2 -d . -classpath $(CLASSPATH):/usr/local/src/jdk1.2.2/jre/lib/rt.jar
LEASE_CLASSES = \
	lease/FileClassifierLeasedResource.class \
	lease/FileClassifierLeaseManager.class \
	lease/FileClassifierLandlord.class \
	lease/FileClassifierImpl.class \
	lease/FileClassifierServer.class \
	lease/RemoteLeaseFileClassifier.class

LEASE_STUB = lease/FileClassifierLandlord_Stub.class

HTML_LEASE = $(HTML_HOME)/$(CODEBASE)/lease/FileClassifierImpl.class \


FOOLANDLORD_CLASSES = \
	foolandlord/Foo.class \
	foolandlord/FooLeasedResource.class \
	foolandlord/FooLeaseManager.class \
	foolandlord/FooLandlord.class \

JERI = \
	jeri/ConfigExportDemo.class \
	jeri/RmiImplicitExportDemo.class \
	jeri/RmiExplicitExportDemo.class \
	jeri/ExportJrmpDemo.class \
	jeri/ExportIiopDemo.class \
	jeri/ExportJeriDemo.class 


JERI_STUB = \
	jeri/RmiImplicitExportDemo_Stub.class \
	jeri/RmiExplicitExportDemo_Stub.class

MEDIA_CLASSES =

# MEDIA_CLASSES = \
#	media/AudioSource.class \
#	media/AudioSink.class

# LEASE NEEDS REVISING
LEASE_CLASSES =
LEASE_STUB =
HTML_LEASE =
FOOLANDLORD_CLASSES =


OLD_CLASSES = \
	$(MEDIA_CLASSES) \
	\
	$(JERI) \
	\
	basic/InvalidLookupLocator.class \
	basic/UnicastRegister.class \
	basic/UnicastLocalRegister.class \
	basic/MulticastRegister.class \
	basic/MulticastRegisterLogger.class \
	basic/SimpleService.class \
	\
	standalone/MIMEType.class \
	standalone/FileClassifier.class \
	standalone/NameEntry.class \
	standalone/TestRCX.class \
	\
	common/MIMEType.class \
	common/FileClassifier.class \
	common/NameEntry.class \
	common/Printer.class \
	common/Distance.class \
	common/LeaseFileClassifier.class \
	common/MutableFileClassifier.class \
	common/Accounts.class \
	common/ServiceFinder.class \
	\
	client/TestFileClassifier.class \
	client/TestUnicastFileClassifier.class \
	client/TestFileClassifierThread.class \
	client/TestNameEntry.class \
	client/TestPrinterSpeed.class \
	client/TestPrinterDistance.class \
	client/TestFileClassifierLease.class \
	client/TestCorbaHello.class \
	client/ReggieMonitor.class \
	client/ServiceMonitor.class \
	client/ImmediateClientLookup.class \
	client/CachedClientLookup.class \
	client/TestPrinterSpeedFilter.class \
	client/TestFrameUI.class \
	\
	discoverymgt/UnicastRegister.class \
	discoverymgt/AllcastRegister.class \
	\
	complete/FileClassifierImpl.class \
	complete/FileClassifierServer.class \
	complete/FileClassifierServerID.class \
	\
	rmi/RemoteFileClassifier.class \
	rmi/FileClassifierImpl.class \
	rmi/FileClassifierProxy.class \
	rmi/FileClassifierServer.class \
	\
	joinmgr/FileClassifierServer.class \
	\
	printer/Printer20.class \
	printer/Printer30.class \
	\
	complex/NameEntryImpl1.class \
	complex/NameEntryImpl2.class \
	complex/NameEntryImpl3.class \
	complex/Server1.class \
	complex/Server2.class \
	complex/Server3.class \
	complex/FileClassifierServer.class \
	complex/PrinterServerSpeed.class \
	complex/PrinterServerLocation.class \
	complex/DistanceImpl.class \
	complex/Size.class \
	\
	rmi/FileClassifierServerRMI.class \
	rmi/FileClassifierNamingProxy.class \
	rmi/FileClassifierServerNaming.class \
	\
	heart/Heart.class \
	heart/HeartImpl.class \
	heart/HeartServer.class \
	heart/HeartClient.class \
	\
	ui/FileClassifierFrame.class \
	ui/FileClassifierFrameFactory.class \
	ui/FileClassifierServer.class \
	\
	unique/FileClassifierServer.class \
	unique/TestFileClassifier.class \
	\
	$(LEASE_CLASSES) \
	\
	mutable/RemoteFileClassifier.class \
	mutable/FileClassifierImpl.class \
	mutable/FileClassifierProxy.class \
	mutable/FileClassifierServer.class \
	\
	$(FOOLANDLORD_CLASSES) \
	\
	hostile/HostileFileClassifier1.class \
	hostile/HostileFileClassifier2.class \
	\
	common/Payable.class \
	common/PayableFileClassifier.class \
	txn/RemoteAccounts.class\
	txn/AccountsImpl.class\
	txn/AccountsServer.class \
	client/TestTxn.class \
	txn/RemotePayableFileClassifier.class \
	txn/PayableFileClassifierImpl.class \
	txn/FileClassifierServer.class \
	\
	corba/HelloApp/Hello.class \
	corba/HelloApp/HelloHelper.class \
	corba/HelloApp/HelloHolder.class \
	corba/HelloApp/_HelloImplBase.class \
	corba/HelloApp/_HelloStub.class \
	corba/CorbaHelloServer.class \
	corba/CorbaHelloClient.class \
	corba/JavaHello.class \
	corba/JavaHelloImpl.class \
	corba/JavaHelloServer.class \
	corba/RoomBooking/MaxSlots.class \
	corba/RoomBooking/Meeting.class \
	corba/RoomBooking/MeetingFactory.class \
	corba/RoomBooking/MeetingFactoryHelper.class \
	corba/RoomBooking/MeetingFactoryHolder.class \
	corba/RoomBooking/MeetingHelper.class \
	corba/RoomBooking/MeetingHolder.class \
	corba/RoomBooking/NoMeetingInThisSlot.class \
	corba/RoomBooking/NoMeetingInThisSlotHelper.class \
	corba/RoomBooking/NoMeetingInThisSlotHolder.class \
	corba/RoomBooking/Room.class \
	corba/RoomBooking/RoomHelper.class \
	corba/RoomBooking/RoomHolder.class \
	corba/RoomBooking/Slot.class \
	corba/RoomBooking/SlotAlreadyTaken.class \
	corba/RoomBooking/SlotAlreadyTakenHelper.class \
	corba/RoomBooking/SlotAlreadyTakenHolder.class \
	corba/RoomBooking/SlotHelper.class \
	corba/RoomBooking/SlotHolder.class \
	corba/RoomBooking/_MeetingFactoryImplBase.class \
	corba/RoomBooking/_MeetingFactoryStub.class \
	corba/RoomBooking/_MeetingImplBase.class \
	corba/RoomBooking/_MeetingStub.class \
	corba/RoomBooking/_RoomImplBase.class \
	corba/RoomBooking/_RoomStub.class \
	corba/RoomBookingImpl/MeetingFactoryImpl.class \
	corba/RoomBookingImpl/MeetingFactoryServer.class \
	corba/RoomBookingImpl/MeetingImpl.class \
	corba/common/JavaMeeting.class \
	corba/RoomBookingImpl/JavaMeetingImpl.class \
	corba/common/JavaRoom.class \
	corba/RoomBookingImpl/JavaRoomImpl.class \
	corba/common/RoomBookingBridge.class \
	corba/RoomBookingImpl/RemoteRoomBookingBridge.class \
	corba/RoomBookingImpl/RoomBookingBridgeImpl.class \
	corba/RoomBookingImpl/RoomBookingBridgeServer.class \
	corba/RoomBookingImpl/RoomBookingClient.class \
	corba/RoomBookingImpl/RoomBookingClientApplication.class \
	corba/RoomBookingImpl/RoomImpl.class \
	corba/RoomBookingImpl/RoomServer.class \
	\
	observer/RegistrarObserver.class \
	\
	socket/FileClassifierProxy.class \
	socket/FileServerImpl.class \
	socket/FileClassifierServer.class \
	\
	extended/RemoteExtendedFileClassifier.class \
	extended/ExtendedFileClassifierImpl.class \
	extended/FileClassifierProxy.class \
	extended/FileClassifierServer.class \
	\
	activation/FileClassifierImpl.class \
	activation/FileClassifierMutable.class \
	activation/FileClassifierServer.class \
	activation/FileClassifierServerMutable.class \
	activation/FileClassifierServerLease.class \
	activation/FileClassifierServerDiscovery.class \
	activation/RenewLease.class \
	activation/DiscoveryChange.class \
	activation/Buggy.class \
	\
	servlet/FileClassifierServlet.class \
	\
	nameserver/NameServer.class \
	nameserver/GetService.class \
	\
	jnlp/FileClassifierApplication.class \
	\
	$(RCX_CLASSES) \
	\
	$(CGI_CLASSES) \
	\
	$(WEBLOGIC_CLASSES)


# These classes need to be known to the HTTP server
# for copying across to clients
OLD_HTML_CLASSES = \
	$(HTML_HOME)/$(CODEBASE)/complete/FileClassifierImpl.class \
	$(HTML_HOME)/$(CODEBASE)/rmi/FileClassifierImpl.class \
	$(HTML_HOME)/$(CODEBASE)/rmi/RemoteFileClassifier.class \
	$(HTML_HOME)/$(CODEBASE)/rmi/FileClassifierProxy.class \
	$(HTML_HOME)/$(CODEBASE)/common/MutableFileClassifier.class \
	$(HTML_HOME)/$(CODEBASE)/mutable/RemoteFileClassifier.class \
	$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl1.class \
	$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl2.class \
	$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl3.class \
	$(HTML_LEASE) \
	$(HTML_HOME)/$(CODEBASE)/lease/RemoteLeaseFileClassifier.class \
	$(HTML_HOME)/$(CODEBASE)/rcx/jini/RCXLoaderFrame.class \
	$(HTML_HOME)/images/mindstorms.jpg \
	$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierImpl.class \
	$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierMutable.class \
	$(HTML_HOME)/$(CODEBASE)/activation/RenewLease.class \
	$(HTML_HOME)/rcx/Car.html \
	$(HTML_HOME)/ui/FileClassifier.pl \
	$(HTML_HOME)/../servlets/servlet/FileClassifierServlet.class \
	$(HTML_HOME)/$(CODEBASE)/jnlp/FileClassifierApplication.jar \
	$(HTML_HOME)/jnlp/FileClassifierApplication.jnlp

#	$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierServerLease$$RenewLease.class


# These files are copies, installed in their directory so
# that clients can be tested in their own minimal environment

CLIENT_CLASSES = \
	$(CLIENT_HOME)/common/MIMEType.class \
	$(CLIENT_HOME)/common/FileClassifier.class \
	$(CLIENT_HOME)/common/LeaseFileClassifier.class \
	$(CLIENT_HOME)/common/MutableFileClassifier.class \
	$(CLIENT_HOME)/common/NameEntry.class \
	$(CLIENT_HOME)/common/Printer.class \
	$(CLIENT_HOME)/common/Distance.class \
	\
	$(CLIENT_HOME)/client/TestFileClassifier.class \
	$(CLIENT_HOME)/client/TestFileClassifierThread.class \
	$(CLIENT_HOME)/client/TestFileClassifierLease.class \
	$(CLIENT_HOME)/client/TestNameEntry.class \
	$(CLIENT_HOME)/client/TestPrinterDistance.class \
	$(CLIENT_HOME)/client/TestPrinterSpeed.class \
	$(CLIENT_HOME)/client/TestRCX.class

SERVLET_CLASSES = $(SERVLET_CLASS_PATH)/FileClassifierServlet.class
SERVLET_COMMON_CLASSES = \
	$(SERVLET_COMMON_CLASS_PATH)/common/FileClassifier.class \
	$(SERVLET_COMMON_CLASS_PATH)/common/MIMEType.class


# These classes need to have rmic run on them as they
# are exported to RMI
STUBS = rmi/FileClassifierImpl_Stub.class \
	$(JERI_STUB) \
	rcx/jini/RCXPortImpl_Stub.class \
	client/TestRCX$$EventHandler_Stub.class \
	client/TestRCX3$$EventHandler_Stub.class \
	$(LEASE_STUB) \
	txn/AccountsImpl_Stub.class \
	txn/PayableFileClassifierImpl_Stub.class \
	client/TestTxn$$LookupThread_Stub.class \
	corba/RoomBookingImpl/RoomBookingBridgeImpl_Stub.class \
	observer/RegistrarObserver_Stub.class \
	extended/ExtendedFileClassifierImpl_Stub.class \
	rcx/jini/NotQuiteCImpl_Stub.class \
	rcx/jini/RCXLoaderFrame_Stub.class \
	activation/FileClassifierImpl_Stub.class \
	activation/FileClassifierMutable_Stub.class \
	activation/RenewLease_Stub.class \
	activation/DiscoveryChange_Stub.class

#	activation/FileClassifierServerLease$$RenewLease_Stub.class

DOCS = \
	Changes.html \
	Overview.html \
	TroubleShooting.html \
	LookupDiscovery.html \
	Entry.html \
	ServiceRegistration.html \
	ClientSearch.html \
	SimpleExample.html \
	Security.html \
	Lease.html \
	DiscoveryMgt.html \
	JoinManager.html \
	Choices.html \
	Jeri.html \
	Config.html \
	Logger.html \
	MoreComplex.html \
	Architecture.html \
	Event.html \
	Audio.html \
	Activation.html \
	ServiceDiscoveryManager.html \
	MindStorms.html \
	Transaction.html \
	Corba.html \
	UI.html \
	EJB.html \
	ServiceDiscoveryManager.html \
	Jini.html \
	Ant.html \
	ServiceStarter.html \
	AdvancedSecurity.html \
	Introspection.html \
	FlashingClocks.html \
	WebServices.html

ZIPS = programs.zip docs.zip classes.zip

HTML_UI = \
	ui/FileClassifier.pl \
	ui/FileClassifierRequest.sh \
	ui/FileClassifierRequest.java \
	ui/FileClassifierServer.java \
	ui/HTMLFileClassifierFactory.java \
	net/jini/lookup/ui/factory/HTMLFactory.java \
	servlet/FileClassifierServlet.java

TESTS =

all: $(CLASSES) $(DOCS) $(ZIPS)
	make classes

docs: $(DOCS)

classes: $(CLASSES) $(HTML_CLASSES) $(CLIENT_CLASSES) $(STUBS) $(SERVLET_CLASSES) $(SERVLET_COMMON_CLASSES)

programs.zip:
	zip programs.zip Makefile `find src -name "*.java" -print`\
	`find . -name "*.sh" -print` \
	`find . -name "*.pl" -print` \
	`find . -name "*.config" -print` \
	run policy* \
        build.xml antBuildFiles/*.xml \
        images/clock.jpg resources/images/clock.jpg \
        lib/*

#classes.zip:
#	make classes
#	zip classes.zip Makefile `find . -name "*.class" -print` \
#	`find . -name "*.jar" -print`

classes.zip:
	ant compile
	zip -r classes.zip build dist

docs.zip: $(DOCS)
	rm -f docs.zip
	zip docs.zip *.pl *.xml *.html copyright.txt images/*.gif

postscript.zip: $(DOCS)
	(for i in $(DOCS); do html2ps $$i > $$i.ps; done)
	zip postscript.zip *.html.ps
	rm *.html.ps

HTMLui.zip: $(HTML_UI)
	zip HTMLui.zip $(HTML_UI)

distrib: 
	make clean
	make docs.zip
	make programs.zip
	make classes.zip
	make postscript.zip
	(cd ..; tar cf tutorial.tar tutorial; rm -f tutorial.tar.gz; gzip tutorial.tar)

Jini.xml: preJini.xml headers.pl
	headers.pl < preJini.xml > Jini.xml



#
# Stub relations
#
rmi/FileClassifierImpl_Stub.class : rmi/FileClassifierImpl.class
	rmic $(RMIC_ARGS) rmi.FileClassifierImpl
	cp -P rmi/FileClassifierImpl_Stub.class $(HTML_HOME)/$(CODEBASE)
	cp -P rmi/RemoteFileClassifier.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/FileClassifier.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/MIMEType.class $(HTML_HOME)/$(CODEBASE)

rcx/jini/RCXPortImpl_Stub.class : rcx/jini/RCXPortImpl.class
	rmic $(RMIC_ARGS) rcx.jini.RCXPortImpl
	cp -P rcx/jini/RCXPortImpl_Stub.class $(HTML_HOME)/$(CODEBASE)

client/TestRCX$$EventHandler_Stub.class : client/TestRCX.class
	rmic $(RMIC_ARGS) client.TestRCX\$$EventHandler
	cp -P client/TestRCX\$$EventHandler_Stub.class $(HTML_HOME)/$(CODEBASE)

client/TestRCX3$$EventHandler_Stub.class : client/TestRCX3.class
	rmic $(RMIC_ARGS) client.TestRCX3\$$EventHandler
	cp -P client/TestRCX3\$$EventHandler_Stub.class $(HTML_HOME)/$(CODEBASE)

client/TestTxn$$LookupThread_Stub.class : client/TestTxn.class
	rmic $(RMIC_ARGS) client.TestTxn\$$LookupThread
	cp -P client/TestTxn\$$LookupThread_Stub.class $(HTML_HOME)/$(CODEBASE)

corba/RoomBookingImpl/RoomBookingBridgeImpl_Stub.class : corba/RoomBookingImpl/RoomBookingBridgeImpl.class
	rmic  $(RMIC_ARGS) corba.RoomBookingImpl.RoomBookingBridgeImpl
	cp -P corba/RoomBookingImpl/RoomBookingBridgeImpl_Stub.class $(HTML_HOME)/$(CODEBASE)

lease/FileClassifierImpl_Stub.class : lease/FileClassifierImpl.class
	rmic $(RMIC_ARGS) lease.FileClassifierImpl
	cp -P lease/FileClassifierImpl_Stub.class $(HTML_HOME)/$(CODEBASE)

lease/FileClassifierLandlord_Stub.class : lease/FileClassifierLandlord.class
	rmic $(RMIC_ARGS) lease.FileClassifierLandlord
	cp -P lease/FileClassifierLandlord_Stub.class $(HTML_HOME)/$(CODEBASE)

txn/AccountsImpl_Stub.class : txn/AccountsImpl.class
	rmic $(RMIC_ARGS) txn.AccountsImpl
	cp -P txn/AccountsImpl_Stub.class $(HTML_HOME)/$(CODEBASE)
	cp -P txn/RemoteAccounts.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/Accounts.class $(HTML_HOME)/$(CODEBASE)

txn/PayableFileClassifierImpl_Stub.class : txn/PayableFileClassifierImpl.class
	rmic $(RMIC_ARGS) txn.PayableFileClassifierImpl
	cp -P txn/PayableFileClassifierImpl_Stub.class $(HTML_HOME)/$(CODEBASE)
	cp -P txn/RemotePayableFileClassifier.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/PayableFileClassifier.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/FileClassifier.class $(HTML_HOME)/$(CODEBASE)
	cp -P common/Payable.class $(HTML_HOME)/$(CODEBASE)

observer/RegistrarObserver_Stub.class : observer/RegistrarObserver.class
	rmic $(RMIC_ARGS) observer.RegistrarObserver
	cp -P observer/RegistrarObserver_Stub.class $(HTML_HOME)/$(CODEBASE)

extended/ExtendedFileClassifierImpl_Stub.class : extended/ExtendedFileClassifierImpl.class 
	rmic $(RMIC_ARGS) extended.ExtendedFileClassifierImpl
	cp -P extended/ExtendedFileClassifierImpl_Stub.class $(HTML_HOME)/$(CODEBASE)
	cp -P extended/RemoteExtendedFileClassifier.class $(HTML_HOME)/$(CODEBASE)

rcx/jini/NotQuiteCImpl_Stub.class : rcx/jini/NotQuiteCImpl.class
	rmic $(RMIC_ARGS) rcx.jini.NotQuiteCImpl
	cp -P rcx/jini/NotQuiteCImpl_Stub.class $(HTML_HOME)/$(CODEBASE)

rcx/jini/RCXLoaderFrame_Stub.class : rcx/jini/RCXLoaderFrame.class
	rmic $(RMIC_ARGS) rcx.jini.RCXLoaderFrame
	cp -P rcx/jini/RCXLoaderFrame_Stub.class $(HTML_HOME)/$(CODEBASE)

activation/FileClassifierImpl_Stub.class : activation/FileClassifierImpl.class
	rmic  $(RMIC_ARGS) activation.FileClassifierImpl
	cp -P activation/FileClassifierImpl_Stub.class  $(HTML_HOME)/$(CODEBASE)

activation/FileClassifierMutable_Stub.class : activation/FileClassifierMutable.class
	rmic  $(RMIC_ARGS) activation.FileClassifierMutable
	cp -P activation/FileClassifierMutable_Stub.class  $(HTML_HOME)/$(CODEBASE)

activation/RenewLease_Stub.class : activation/RenewLease.class
	rmic  $(RMIC_ARGS) activation.RenewLease
	cp -P activation/RenewLease_Stub.class  $(HTML_HOME)/$(CODEBASE)

#activation/FileClassifierServerLease$$RenewLease_Stub.class : activation/FileClassifierServerLease.class
#	rmic  $(RMIC_ARGS) activation.FileClassifierServerLease\$$RenewLease
#	cp -P activation/FileClassifierServerLease\$$RenewLease_Stub.class  $(HTML_HOME)/$(CODEBASE)

activation/DiscoveryChange_Stub.class : activation/DiscoveryChange.class
	rmic  $(RMIC_ARGS) activation.DiscoveryChange
	cp -P activation/DiscoveryChange_Stub.class  $(HTML_HOME)/$(CODEBASE)

jeri/JeriExportDemo_Stub.class : jeri/JeriExportDemo.class
	rmic  $(RMIC_ARGS) jeri.JeriExportDemo

jeri/RmiImplicitExportDemo_Stub.class : jeri/RmiImplicitExportDemo.class
	rmic  $(RMIC_ARGS) jeri.RmiImplicitExportDemo

jeri/RmiExplicitExportDemo_Stub.class : jeri/RmiExplicitExportDemo.class
	rmic  $(RMIC_ARGS) jeri.RmiExplicitExportDemo

#
#
# HTML dependencies
#
$(HTML_HOME)/$(CODEBASE)/complete/FileClassifierImpl.class: complete/FileClassifierImpl.class
	cp -P complete/FileClassifierImpl.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/rmi/FileClassifierImpl.class: rmi/FileClassifierImpl.class
	cp -P rmi/FileClassifierImpl.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/rmi/RemoteFileClassifier.class: rmi/RemoteFileClassifier.class
	cp -P rmi/RemoteFileClassifier.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/rmi/FileClassifierProxy.class: rmi/FileClassifierProxy.class
	cp -P rmi/FileClassifierProxy.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/mutable/RemoteFileClassifier.class: mutable/RemoteFileClassifier.class
	cp -P mutable/RemoteFileClassifier.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/common/MutableFileClassifier.class: common/MutableFileClassifier.class
	cp -P common/MutableFileClassifier.class $(HTML_HOME)/$(CODEBASE)


$(HTML_HOME)/$(CODEBASE)/lease/FileClassifierImpl.class: lease/FileClassifierImpl.class
	cp -P lease/FileClassifierImpl.class $(HTML_HOME)/$(CODEBASE)


$(HTML_HOME)/$(CODEBASE)/lease/RemoteLeaseFileClassifier.class: lease/RemoteLeaseFileClassifier.class
	cp -P lease/RemoteLeaseFileClassifier.class $(HTML_HOME)/$(CODEBASE)
$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl1.class: complex/NameEntryImpl1.class
	cp -P complex/NameEntryImpl1.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl2.class: complex/NameEntryImpl2.class
	cp -P complex/NameEntryImpl2.class $(HTML_HOME)/$(CODEBASE)
	cp -P complex/NameEntryImpl2\$$1.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/complex/NameEntryImpl3.class: complex/NameEntryImpl3.class
	cp -P complex/NameEntryImpl3.class $(HTML_HOME)/$(CODEBASE)
	cp -P complex/NameEntryImpl3\$$1.class $(HTML_HOME)/$(CODEBASE)
	cp -P complex/NameHandler.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/extended/FileClassifierProxy.class: \
	extended/ExtendedFileClassifierProxy.class
	cp -P extended/ExtendedFileClassifierProxy.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/rcx/jini/RCXLoaderFrame.class: \
	rcx/jini/RCXLoaderFrame.class
	cp -P rcx/jini/RCXLoaderFrame.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/images/mindstorms.jpg: \
	images/mindstorms.jpg
	cp -P images/mindstorms.jpg $(HTML_HOME)

$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierImpl.class: \
	activation/FileClassifierImpl.class
	cp -P activation/FileClassifierImpl.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierMutable.class: \
	activation/FileClassifierMutable.class
	cp -P activation/FileClassifierMutable.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierServerLease.class: \
	activation/FileClassifierServerLease.class
	cp -P activation/FileClassifierServerLease.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/$(CODEBASE)/activation/RenewLease.class : \
	activation/RenewLease.class
	cp -P activation/RenewLease.class  $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/rcx/Car.html : rcx/jini/Car.html
	cp rcx/jini/Car.html $(HTML_HOME)/rcx

$(HTML_HOME)/ui/FileClassifier.pl : ui/FileClassifier.pl
	cp ui/FileClassifier.pl $(HTML_HOME)/../cgi-bin
	chmod a+x $(HTML_HOME)/../cgi-bin/FileClassifier.pl

$(HTML_HOME)/ui/FileClassifierRequest.sh : ui/FileClassifierRequest.sh
	cp ui/FileClassifierRequest.sh $(HTML_HOME)/../cgi-bin
	chmod a+x $(HTML_HOME)/../cgi-bin/FileClassifierRequest.sh

$(HTML_HOME)/$(CODEBASE)/ui/FileClassifierRequest.class : ui/FileClassifierRequest.class
	cp -P ui/FileClassifierRequest.class $(HTML_HOME)/$(CODEBASE)

$(HTML_HOME)/../servlets/servlet/FileClassifierServlet.class : servlet/FileClassifierServlet.class
	cp -P servlet/FileClassifierServlet.class $(HTML_HOME)/../servlets

#$(HTML_HOME)/$(CODEBASE)/activation/FileClassifierServerLease$$RenewLease.class : \
#	activation/FileClassifierServerLease.class
#	cp -P activation/FileClassifierServerLease\$$RenewLease.class  $(HTML_HOME)/$(CODEBASE)



# $(HTML_HOME)/$(CODEBASE)/lease/FileClassifierLandlord.class: lease/FileClassifierLandlord.class
#	cp -P lease/FileClassifierLandlord.class $(HTML_HOME)/$(CODEBASE)

#
# Client classes
#
$(CLIENT_HOME)/common/MIMEType.class: common/MIMEType.class
	cp -P common/MIMEType.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/FileClassifier.class: common/FileClassifier.class
	cp -P common/FileClassifier.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/NameEntry.class: common/NameEntry.class
	cp -P common/NameEntry.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/Printer.class: common/Printer.class
	cp -P common/Printer.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/Distance.class: common/Distance.class
	cp -P common/Distance.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/LeaseFileClassifier.class: common/LeaseFileClassifier.class
	cp -P common/LeaseFileClassifier.class $(CLIENT_HOME)

$(CLIENT_HOME)/common/MutableFileClassifier.class: common/MutableFileClassifier.class
	cp -P common/MutableFileClassifier.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestFileClassifier.class: client/TestFileClassifier.class
	cp -P client/TestFileClassifier.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestFileClassifierThread.class: client/TestFileClassifierThread.class
	cp -P client/TestFileClassifierThread.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestFileClassifierLease.class: client/TestFileClassifierLease.class
	cp -P client/TestFileClassifierLease.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestNameEntry.class: client/TestNameEntry.class
	cp -P client/TestNameEntry.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestPrinterDistance.class: client/TestPrinterDistance.class
	cp -P client/TestPrinterDistance.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestPrinterSpeed.class: client/TestPrinterSpeed.class
	cp -P client/TestPrinterSpeed.class $(CLIENT_HOME)

$(CLIENT_HOME)/client/TestRCX.class: client/TestRCX.class
	cp -P client/TestRCX.class $(CLIENT_HOME)

#
# Servlet classes
#
$(SERVLET_CLASS_PATH)/FileClassifierServlet.class : servlet/FileClassifierServlet.class
	cp -P servlet/FileClassifierServlet.class $(SERVLET_CLASS_PATH)

$(SERVLET_COMMON_CLASS_PATH)/common/FileClassifier.class : common/FileClassifier.class
	cp -P common/FileClassifier.class $(SERVLET_COMMON_CLASS_PATH)

$(SERVLET_COMMON_CLASS_PATH)/common/MIMEType.class : common/MIMEType.class
	cp -P common/MIMEType.class $(SERVLET_COMMON_CLASS_PATH)



#
# JNLP
#
$(HTML_HOME)/$(CODEBASE)/jnlp/FileClassifierApplication.jar: jnlp/FileClassifierApplication.class
	jar -cf FileClassifierApplication.jar \
	        jnlp/*.class \
	        common/FileClassifier.class \
	        common/MIMEType.class
	# jarsigner -keystore $$HOME/.keystore -signedjar sFileClassifierApplication.jar FileClassifierApplication.jar Jan
	# mv sFileClassifierApplication.jar $(HTML_HOME)/$(CODEBASE)/jnlp

$(HTML_HOME)/jnlp/FileClassifierApplication.jnlp: jnlp/FileClassifierApplication.jnlp
	cp jnlp/FileClassifierApplication.jnlp $(HTML_HOME)/jnlp

#
# Corba dependencies
#
corba/HelloApp/Hello.java : corba/Hello.idl
	idltojava corba/Hello.idl

#
# EJB dependencies, using BEA Weblogic
#

ejb/META-INF/ejb-jar.xml : ejb/FileClassifierDD.txt
	java weblogic.ejb.utils.DDConverter -d ejb/META-INF ejb/FileClassifierDD.txt
	mv ejb/META-INF .; jar cf EJBFileClassifier.jar ejb/*.class META-INF; mv META-INF ejb

#
# additional dependencies between classes
#
client/TestFileClassifier.class :: \
	common/FileClassifier.class \
	common/MIMEType.class

client/TestFileClassifierThread.class :: \
	common/FileClassifier.class \
	common/MIMEType.class

client/TestFileClassifierLease.class :: \
	common/LeaseFileClassifier.class \
	common/MIMEType.class

client/TestNameEntry.class :: \
	common/NameEntry.class

client/TestPrinterDistance.class :: \
	common/Printer.class \
	common/Distance.class

client/TestPrinterSpeed.class :: \
	common/Printer.class

client/TestRCX.class :: \
	rcx/jini/RCXPortInterface.class

client/TestCorbaHello.class :: \
	corba/JavaHello.class

common/FileClassifier.class :: \
	common/MIMEType.class

common/LeaseFileClassifier.class :: \
	common/MIMEType.class

common/MutableFileClassifier.class :: \
	common/MIMEType.class

complex/DistanceImpl.class :: \
	common/Distance.class

complex/FileClassifierServer.class :: \
	complete/FileClassifierImpl.class

complex/FileClassifierServerRMI.class :: \
	rmi/FileClassifierImpl.class \
	rmi/RemoteFileClassifier.class

complex/NameEntryImpl1.class :: \
	common/NameEntry.class

complex/NameEntryImpl2.class :: \
	common/NameEntry.class

complex/NameEntryImpl3.class :: \
	common/NameEntry.class

complex/PrinterServerLocation.class :: \
	printer/Printer20.class \
	printer/Printer30.class \
	complex/DistanceImpl.class

complex/PrinterServerSpeed.class :: \
	printer/Printer20.class \
	printer/Printer30.class

complex/PrinterServerSpeed.class :: \
	common/Printer.class

complex/Server1.class :: \
	complex/NameEntryImpl1.class

complex/Server2.class :: \
	complex/NameEntryImpl2.class

complex/Server3.class :: \
	complex/NameEntryImpl3.class

complex/Size.class :: \
	complex/NameEntryImpl1.class

corba/RoomBookingImpl/RoomBookingClient.class :: \
	corba/RoomBookingImpl/RoomBookingBridgeImpl.class

corba/RoomBookingImpl/RoomBookingBridgeImpl.class :: \
	corba/common/RoomBookingBridge.class

corba/common/RoomBookingBridge.class :: \
	corba/common/JavaRoom.class \
	corba/common/JavaMeeting.class

extended/RemoteExtendedFileClassifier.class :: \
	common/ExtendedFileClassifier.class

extended/ExtendedFileClassifierImpl.class :: \
	extended/RemoteExtendedFileClassifier.class

extended/FileClassifierProxy.class :: \
	common/FileClassifier.class \
	extended/ExtendedFileClassifierImpl.class

extended/FileClassifierServer.class :: \
	extended/ExtendedFileClassifierImpl.class \
	extended/FileClassifierProxy.class

foolandlord/FooLandlord.class :: \
	foolandlord/Foo.class

foolandlord/FooLeaseManager.class :: \
	foolandlord/FooLeasedResource.class

foolandlord/FooLeasedResource.class :: \
	foolandlord/Foo.class

heart/HeartImpl.class :: \
	heart/Heart.class

heart/HeartServer.class :: \
	heart/HeartImpl.class

heart/HeartClient.class :: \
	heart/Heart.class

heart/HeartImpl.class :: \
	heart/Heart.class

joinmgr/FileClassifierServer.class :: \
	complete/FileClassifierImpl.class

lease/FileClassifierImpl.class :: \
	common/LeaseFileClassifier.class \
	lease/RemoteLeaseFileClassifier.class

lease/FileClassifierLandlord.class :: \
	common/LeaseFileClassifier.class \
	lease/FileClassifierLeasedResource.class \
	lease/FileClassifierLeaseManager.class

lease/FileClassifierLeaseManager.class :: \
	lease/FileClassifierLeasedResource.class

lease/FileClassifierLeasedResourceImpl.class :: \
	common/LeaseFileClassifier.class

lease/FileClassifierServer.class :: \
	lease/FileClassifierImpl.class

lease/RemoteLeaseFileClassifier.class :: \
	common/LeaseFileClassifier.class

mutable/FileClassifierImpl.class :: \
	common/MIMEType.class \
	common/MutableFileClassifier.class

mutable/FileClassifierProxy.class :: \
	common/MutableFileClassifier.class \
	common/MIMEType.class \
	mutable/FileClassifierImpl.class

mutable/FileClassifierServer.class :: \
	mutable/FileClassifierImpl.class \
	mutable/FileClassifierProxy.class

mutable/RemoteFileClassifier.class :: \
	common/MutableFileClassifier.class

complete/FileClassifierServer.class :: \
	complete/FileClassifierImpl.class

complete/FileClassifierImpl.class :: \
	common/MIMEType.class \
	common/FileClassifier.class \

rmi/FileClassifierServer.class :: \
	rmi/FileClassifierImpl.class \
	rmi/FileClassifierProxy.class \

rmi/FileClassifierImpl.class :: \
	rmi/RemoteFileClassifier.class \
	common/MIMEType.class

rmi/FileClassifierProxy.class :: \
	rmi/FileClassifierImpl.class \
	common/MIMEType.class

rmi/RemoteFileClassifier.class :: \
	common/FileClassifier.class


printer/Printer20.class :: \
	common/Printer.class

printer/Printer30.class :: \
	common/Printer.class

rcx/jini/RCXPortImpl.class :: \
	rcx/jini/RemoteRCXPort.class

rcx/jini/RemoteRCXPort.class :: \
	rcx/jini/RCXPortInterface.class

rcx/jini/RCXPortProxy.class :: \
	rcx/jini/RCXPortInterface.class

rcx/jini/RCXServer.class :: \
	rcx/jini/RCXPortProxy.class \
	rcx/jini/RCXPortImpl.class	

rcx/jini/NotQuiteC.class :: \
	rcx/jini/CompileException.class

rcx/jini/RemoteNotQuiteC.class :: \
	rcx/jini/NotQuiteC.class

rcx/jini/NotQuiteCImpl.class :: \
	rcx/jini/RemoteNotQuiteC.class

rcx/jini/RCXFrameLoaderFactory.class :: \
	rcx/jini/RCXFrameLoader.class \
	ui/AWTFactory.class

socket/FileClassifierServer.class :: \
	socket/FileClassifierProxy.class \
	socket/FileServerImpl.class

socket/FileClassifierProxy.class :: \
	socket/FileServerImpl.class

standalone/FileClassifier.class :: \
	common/MIMEType.class

txn/RemotePayableFileClassifier.class :: \
	common/Payable.class

txn/FileClassifierServer :: \
	txn/RemotePayableFileClassifier.class \
	txn/AccountsImpl.class

txn/RemoteAccounts.class :: \
	common/Accounts.class

txn/AccountsImpl.class :: \
	txn/RemoteAccounts.class

txn/PayableFileClassifierImpl.class :: \
	txn/RemotePayableFileClassifier.class

txn/AccountsServer.class :: \
	txn/AccountsImpl.class

client/TestTxn.class :: \
	common/PayableFileClassifier.class

ui/AWTFrameFactory.class :: \
	ui/UIFactory.class

unique/FileClassifierServer.class :: \
	rmi/FileClassifierImpl.class \
	rmi/FileClassifierProxy.class

unique/TestFileClassifier.class :: \
	common/FileClassifier.class \
	common/MIMEType.class


clean:
	rm -f `find . -name "*.class" -print` *~ */*~ *.html *.zip core
	rm -rf $(HTML_HOME)/$(CODEBASE)/*
	rm -rf $(CLIENT_HOME)/*
	rm -f ejb/META-INF/*.xml
	rm -rf *~
