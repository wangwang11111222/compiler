ANTLR := /usr/local/lib/antlr-4.13.2-complete.jar
BUILD_DIR := build
JAR_DIR := cs8803_bin
COMPILER_JAR := tigerc.jar
WRAPPER_SCRIPT := tigerc
GRAMMAR := Tiger.g4
MAIN_CLASS_NAME := Main

ANTLR_JAVA_FILES := \
    src/TigerBaseListener.java \
    src/TigerBaseVisitor.java \
    src/TigerLexer.java \
    src/TigerListener.java \
    src/TigerParser.java \
    src/TigerVisitor.java

ANTLR_FILES := \
    src/Tiger.interp \
    src/Tiger.tokens \
    src/TigerLexer.interp \
    src/TigerLexer.tokens \
    $(ANTLR_JAVA_FILES)

ANTLR_LIBS := \
	$(BUILD_DIR)/org

SOURCES := \
    src/Main.java

.PHONY :
all : $(JAR_DIR)/$(WRAPPER_SCRIPT)


$(JAR_DIR)/$(COMPILER_JAR): $(SOURCES) $(ANTLR_JAVA_FILES) $(ANTLR_LIBS)
	@mkdir -p $(BUILD_DIR) $(JAR_DIR)
	@javac -Xlint:deprecation -d $(BUILD_DIR) -cp "src:$(ANTLR)" $(SOURCES) \
	$(ANTLR_JAVA_FILES)
	@cd $(BUILD_DIR) && jar cfe ../$(JAR_DIR)/$(COMPILER_JAR) \
	$(MAIN_CLASS_NAME) *.class org && cd ..


$(JAR_DIR)/$(WRAPPER_SCRIPT): $(JAR_DIR)/$(COMPILER_JAR)
	@echo '#!/bin/sh' > $(JAR_DIR)/$(WRAPPER_SCRIPT)
	@echo 'java -jar $$(dirname "$$0")/$(COMPILER_JAR) "$$@"' >> $(JAR_DIR)/$(WRAPPER_SCRIPT)
	@chmod +x $(JAR_DIR)/$(WRAPPER_SCRIPT)

$(ANTLR_JAVA_FILES): $(GRAMMAR)
	@java -jar $(ANTLR) -o src -visitor $(GRAMMAR)

$(ANTLR_LIBS):
	@mkdir -p $(BUILD_DIR)
	@cd $(BUILD_DIR) && jar xf $(ANTLR) && cd ..
	@rm -rf $(BUILD_DIR)/META-INF

.PHONY:
clean :
	@rm -f $(JAR_DIR)/$(COMPILER_JAR) $(JAR_DIR)/$(WRAPPER_SCRIPT) $(ANTLR_FILES) \
    $(BUILD_DIR)/*.class
	@rm -rf $(ANTLR_LIBS)