# Using latest [VimClojure](http://kotka.de/projects/clojure/vimclojure.html) development snapshot #

## Vimrc settings ##

```
let vimclojure#ParenRainbow = 1                                                                                                                                                                              
let vimclojure#WantNailgun = 1
```

## Running [NailGun](http://martiansoftware.com/nailgun/index.html) server ##

Make a script. There's also [A leiningen plugin to launch a vimclojure nailgun server.](http://clojars.org/org.clojars.brandonw/lein-nailgun) but I didn't get it working, and just used this to launch:

```
#!/bin/sh
CLASSPATH=$PROJECT_DIR:$PROJECT_DIR/bin:$PROJECT_DIR/lib
java -cp $CLASSPATH -server com.martiansoftware.nailgun.NGServer
```

## Build/install ##

```
$ hg clone http://bitbucket.org/kotarak/vimclojure
```

### Build ###

I figured it was easier to use [Leiningen](http://github.com/technomancy/leiningen) build system than to make the [Gradle](http://www.gradle.org/) build system that [VimClojure](http://kotka.de/projects/clojure/vimclojure.html) uses to work, so I created a project.clj for [VimClojure](http://kotka.de/projects/clojure/vimclojure.html):

```
(defproject org.clojars.emh/vimclojure "2.2.0-SNAPSHOT"                                                                                                                                                      
    :source-path "src/main/clojure"                                                                                                                                                                          
    :compile-path "classes"                                                                                                                                                                                  
    :dependencies                                                                                                                                                                                            
            [                                                                                                                                                                                                
             [org.clojure/clojure "1.2.0-master-SNAPSHOT"]                                                                                                                                                   
             [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]                                                                                                                                                  
             [org.clojars.ato/nailgun "0.7.1"]                                                                                                                                                               
            ]                                                                                                                                                                                                
    :dev-dependencies                                                                                                                                                                                        
    [                                                                                                                                                                                                        
     [lein-clojars "0.5.0-SNAPSHOT"]                                                                                                                                                                         
    ])
```

[Leiningen](http://github.com/technomancy/leiningen) build tool is installed like this:
```
wget http://github.com/technomancy/leiningen/raw/stable/bin/lein
mv lein ~/bin/ && chmod +x ~/bin/lein
sh lein self-install
```

### Install ###

I uploaded the [jar](http://clojars.org/org.clojars.emh/vimclojure) built from the snapshot to [Clojars](http://clojars.org/), so you can add it to Leiningen dev dependencies in project.clj for your project. The other snapshot builds on Clojars were outdated as of this writing. You also need to install the latest Vim files of course. I didn't bother to find out how to do it with Leiningen, so I just read the Gradle build file and did:

```
cp -r autoload indent syntax ftdetect ftplugin doc ftplugin/clojure ~/.vim/
```