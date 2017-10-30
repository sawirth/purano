package ch.sawirth.model;

public class NativeEffect {
    public final String owner;
    public final String name;
    public final String originOwner;
    public final String originName;

    public NativeEffect(String owner, String name, String originOwner, String originName) {
        this.owner = owner;
        this.name = name;
        this.originOwner = originOwner;
        this.originName = originName;
    }
}
