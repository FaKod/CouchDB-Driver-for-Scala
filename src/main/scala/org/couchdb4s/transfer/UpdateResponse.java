package org.couchdb4s.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateResponse extends Root {
    public String rev;
}
