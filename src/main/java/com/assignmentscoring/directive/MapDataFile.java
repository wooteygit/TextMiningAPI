/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.directive;

/**
 *
 * @author wootey02
 */
public class MapDataFile {
    private int ID = 0;
    private String FileName = "";
    private String FileType = "";
    private String FileDes = "";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String FileType) {
        this.FileType = FileType;
    }

    public String getFileDes() {
        return FileDes;
    }

    public void setFileDes(String FileDes) {
        this.FileDes = FileDes;
    }
    
}
