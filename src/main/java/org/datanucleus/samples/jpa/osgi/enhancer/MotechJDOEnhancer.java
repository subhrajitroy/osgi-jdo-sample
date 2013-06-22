package org.datanucleus.samples.jpa.osgi.enhancer;

import org.datanucleus.api.jdo.JDOEnhancer;

import java.util.Properties;

public class MotechJDOEnhancer extends JDOEnhancer {

    public MotechJDOEnhancer(Properties props) {
        super(props);
    }
}
