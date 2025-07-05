package com.example.recruitmentagent.model;

import jakarta.persistence.*;

@Entity
public class Metrics {
    @Id
    private Integer id = 1;
    private Integer cvsProcessed = 0;
    private Integer candidatesAptos = 0;
    private Integer candidatesNoAptos = 0;
    private Integer candidatesRevisionManual = 0;

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCvsProcessed() { return cvsProcessed; }
    public void setCvsProcessed(Integer cvsProcessed) { this.cvsProcessed = cvsProcessed; }
    public Integer getCandidatesAptos() { return candidatesAptos; }
    public void setCandidatesAptos(Integer candidatesAptos) { this.candidatesAptos = candidatesAptos; }
    public Integer getCandidatesNoAptos() { return candidatesNoAptos; }
    public void setCandidatesNoAptos(Integer candidatesNoAptos) { this.candidatesNoAptos = candidatesNoAptos; }
    public Integer getCandidatesRevisionManual() { return candidatesRevisionManual; }
    public void setCandidatesRevisionManual(Integer candidatesRevisionManual) { this.candidatesRevisionManual = candidatesRevisionManual; }
}
