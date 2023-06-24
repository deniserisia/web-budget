package br.com.webbudget.application.components.ui;

public enum ViewState {
    
    LISTING {
        @Override
        public void enterState() {
           
        }
    },
    
    ADDING {
        @Override
        public void enterState() {
            
        }
        
        @Override
        public void save() {
           
        }
    },
    
    EDITING {
        @Override
        public void enterState() {
            
        }
        
        @Override
        public void save() {
            // Logic for saving data in the EDITING state
        }
    },
    
    DELETING {
        @Override
        public void enterState() {
            // Logic for entering the DELETING state
        }
        
        @Override
        public void delete() {
        }
    },
    
    DETAILING {
        @Override
        public void enterState() {
        }
    };

    public abstract void enterState();

    public void save() {
        throw new UnsupportedOperationException("Save operation not supported in this state.");
    }
    
    public void delete() {
        throw new UnsupportedOperationException("Delete operation not supported in this state.");
    }
}
