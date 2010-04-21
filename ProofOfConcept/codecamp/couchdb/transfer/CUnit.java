package codecamp.couchdb.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CUnit extends Root {
    public String Type = CUnit.class.getSimpleName();
    public String unitIdentification;
    public String commandedBy;
}
