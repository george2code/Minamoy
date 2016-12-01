package core.model.settings;


public class Language {
    public String name;
    public String cultureCode;
    public int resource;

    public Language(String name, String code, int resource) {
        this.name = name;
        this.cultureCode = code;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCultureCode() {
        return cultureCode;
    }

    public void setCultureCode(String cultureCode) {
        this.cultureCode = cultureCode;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}