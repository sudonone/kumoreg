package org.kumoricon.model.badge;

import com.vaadin.server.ServiceException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @NotNull
    private String name;
    private String dayText;     // Friday/Saturday/Sunday/Weekend/VIP
    private String stripeColor;
    private String stripeText;
    @NotNull
    private boolean visible;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AgeRange> ageRanges;

    public Badge() {
        visible = true;
        ageRanges = new ArrayList<AgeRange>(4);
    }

    public Badge(String name) {
        this();
        setName(name);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDayText() { return dayText; }
    public void setDayText(String day) { this.dayText = day; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public List<AgeRange> getAgeRanges() { return ageRanges; }
    public void addAgeRange(AgeRange ageRange) { ageRanges.add(ageRange); }
    public void addAgeRange(String name, int minAge, int maxAge, BigDecimal cost, String stripeColor, String stripeText) {
        AgeRange a = new AgeRange(name, minAge, maxAge, cost, stripeColor, stripeText);
        ageRanges.add(a);
    }
    public void addAgeRange(String name, int minAge, int maxAge, double cost, String stripeColor, String stripeText) {
        AgeRange a = new AgeRange(name, minAge, maxAge, cost, stripeColor, stripeText);
        ageRanges.add(a);
    }

    public BigDecimal getCostForAge(Long age) throws ServiceException {
        for (AgeRange ageRange : ageRanges) {
            if (ageRange.isValidForAge(age)) {
                return ageRange.getCost();
            }
        }
        throw new ServiceException("Error: No valid age range found in badge {} for age {}".format(this.name, age));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Badge))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Badge o = (Badge) other;
            return this.getId().equals(o.getId());
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return String.format(name);
    }

}
