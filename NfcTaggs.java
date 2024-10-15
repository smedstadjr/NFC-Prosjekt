package com.example.nfcapplicationlocks;

public class NfcTaggs {
    private int taggId;
    private Locks lock;

    public NfcTaggs(int taggId, Locks lock){
        this.taggId = taggId;
        this.lock = lock;
    }

    public int getTaggId() {
        return taggId;
    }

    public void setTaggId(int taggId) {
        this.taggId = taggId;
    }

    public Locks getLock() {
        return lock;
    }

    public void setLock(Locks lock) {
        this.lock = lock;
    }
}
