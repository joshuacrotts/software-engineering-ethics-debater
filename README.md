
# SWED - Software Engineering Ethics Debater

SWED (Software Engineering Ethics Debater) is an educational argument diagramming tool for the domain of software engineering ethics. SWED provides a set of argument schemes as cognitive building blocks for constructing argument diagrams in this domain.  SWED’s user interface and argument schemes were designed by Dr. Nancy Green, University of North Carolina Greensboro.  SWED is the follow-on to her previous work on [AVIZE](https://github.com/greennl/AVIZE).  SWED was implemented by [Larry Joshua Crotts](https://www.github.com/JoshuaCrotts), a UNCG computer science undergraduate, who refactored AVIZE.

## Running SWED
To run SWED with default argument schemes, simply download the latest release from the swed_release/ folder. A direct link to the executable (Windows) is located [here], whereas the executable JAR file (Windows, MacOS, Linux) is here. Within the swed_release/ folder, there are three subfolders: 

- /cases_xmls/
- /ethics_xmls/
- /scheme_xmls/

Each subfolder contains the necessary XML files to load and create cases, ethics, and argument diagrams.

#### Custom Argument Schemes

If you wish to utilize your own custom argument schemes, add the schemes in an xml file to the /scheme_xmls/ in your working directory.

#### Custom Case Studies

If you wish to utilize your own custom case studies, add the cases in an xml file to the cases_xmls in your working directory.

#### Ethics Resources

If you wish to utilize your own ethics resources, add the cases in an xml file to ethics_xmls your working directory.

## Usage

Please refer to the help files and tutorials in the folders by those names.

## Rebuilding SWED

All source code is located in the /src/ directory. The relevant [F]XML, and java files are under their respective directories. To rebuild the code in NetBeans 8.2, Java 8 is required (either Oracle, Amazon Corretto, or OpenJDK). Any arbitrary flavor of Java 8 is most likely acceptable, as long as JavaFX is included.

Clone the repository to your computer, then open NetBeans. NetBeans 8.2 has Maven support out of the box, so all you have to do is open the project.

## Reporting Bugs

See the Issues Tab.

## Version History
The **master** branch encompasses significant development changes in the project, whereas the **development** branch houses various experimentation and states of progression. This branch is constantly evolving. Rarely (whenever it is best), the master branch is updated to mimic the development branch.
