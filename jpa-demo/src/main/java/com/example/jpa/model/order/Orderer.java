package com.example.jpa.model.order;

import com.example.jpa.model.id.MemberId;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Orderer {

    @AttributeOverrides(@AttributeOverride(name = "id", column = @Column(name = "orderer_id")))
    private MemberId memberId;

    @Column(name = "orderer_name")
    private String name;
}
