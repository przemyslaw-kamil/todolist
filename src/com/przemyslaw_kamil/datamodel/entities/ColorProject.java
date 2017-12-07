package com.przemyslaw_kamil.datamodel.entities;


    public enum ColorProject {

        Red,
        Green,
        Blue,
        Yellow,
        Orange,
        none;

        @Override
        public String toString() {
            return this.name();
        }
    }


