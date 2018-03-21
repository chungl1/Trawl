import subprocess

pathToTomcat = "C:\\Users\\liang\\Documents\\Git\\Trawl\\tomcat" # CUSTOM
pathClasspath = "C:\\Users\\liang\\Documents\\Git\\Trawl\\tomcat\\lib\\servlet-api.jar;C:\\Users\\liang\\Documents\\Git\\Trawl\\tomcat\\webapps\\Trawl\\bin" # CUSTOM

pathToSrc = "\\webapps\\Trawl\\bin\\com\\example\\"
pathToFin = "\\webapps\\Trawl\\WEB-INF\\classes\\com\\example\\"

compileCMD1 = "javac -cp " + pathClasspath + " " + pathToTomcat + pathToSrc + "web\\Director.java"
compileCMD2 = "javac -cp " + pathClasspath + " " + pathToTomcat + pathToSrc + "model\\TrawlExpert.java"

# print(compileCMD1)
# print(compileCMD2)

copyCMD1 = "copy " + pathToTomcat + pathToSrc + "web\\Director.class" + " " + pathToTomcat + pathToFin + "web\\Director.class"
copyCMD2 = "copy " + pathToTomcat + pathToSrc + "model\\TrawlExpert.class" + " " + pathToTomcat + pathToFin + "model\\TrawlExpert.class"

# print(copyCMD1)
# print(copyCMD2)

# Command Processes
subprocess.call(compileCMD1, shell=True)
subprocess.call(compileCMD2, shell=True)
subprocess.call(copyCMD1, shell=True)
subprocess.call(copyCMD2, shell=True)