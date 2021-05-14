package kz.yassy.taxi.data.network.model;

public class HelpLocal {
    private String text;
    private int openType;

    public HelpLocal(String text, int openType) {
        this.text = text;
        this.openType = openType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }
}
