package net.myspring.basic.modules.sys.web.form;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.DictMap;
import net.myspring.common.form.BaseForm;

import java.util.List;

/**
 * Created by admin on 2017/4/1.
 */

public class DictMapForm extends BaseForm<DictMap> {
    private String category;
    private String name;
    private String value;
    private String remarks;
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
