package com.saucelab.listeners;

import com.saucelab.utils.XrayLogger;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.List;

/**
 * Custom ChainTest reporter that attaches reports to Xray.
 */
public class ChainTestReporter implements IReporter {
    
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("[ChainTestReporter] Generating report for " + suites.size() + " suites");
        System.out.println("[ChainTestReporter] Output directory: " + outputDirectory);
        
        // Attach ChainTest report to Xray if enabled
        if (XrayLogger.isXrayEnabled()) {
            String chainTestReport = "target/chaintest/Index.html";
            java.io.File reportFile = new java.io.File(chainTestReport);
            
            if (reportFile.exists()) {
                try {
                    System.out.println("[ChainTestReporter] Attaching ChainTest report to Xray: " + chainTestReport);
                    XrayLogger.attachReportToTestExecution(chainTestReport);
                } catch (Exception e) {
                    System.err.println("[ChainTestReporter] Failed to attach report: " + e.getMessage());
                }
            } else {
                System.out.println("[ChainTestReporter] ChainTest report not found at: " + chainTestReport);
            }
        } else {
            System.out.println("[ChainTestReporter] Xray is not enabled, skipping report attachment.");
        }
    }
}
