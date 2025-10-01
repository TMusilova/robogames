package com.robogames.RoboCupMS.Module.CompetitionEvaluation.Bussiness.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.robogames.RoboCupMS.Business.Enum.ECategory;
import com.robogames.RoboCupMS.Business.Enum.EMatchState;
import com.robogames.RoboCupMS.Business.Enum.EScoreAggregation;
import com.robogames.RoboCupMS.Entity.Competition;
import com.robogames.RoboCupMS.Entity.Discipline;
import com.robogames.RoboCupMS.Entity.Robot;
import com.robogames.RoboCupMS.Entity.RobotMatch;
import com.robogames.RoboCupMS.Entity.ScoreAggregation;
import com.robogames.RoboCupMS.Entity.Team;
import com.robogames.RoboCupMS.Entity.TeamRegistration;
import com.robogames.RoboCupMS.Module.CompetitionEvaluation.Bussiness.Object.OrderObj;
import com.robogames.RoboCupMS.Module.CompetitionEvaluation.Bussiness.Object.RobotScore;
import com.robogames.RoboCupMS.Module.CompetitionEvaluation.Bussiness.Object.TeamScore;
import com.robogames.RoboCupMS.Repository.CompetitionRepository;
import com.robogames.RoboCupMS.Repository.DisciplineRepository;
import com.robogames.RoboCupMS.Repository.RobotRepository;
import com.robogames.RoboCupMS.Repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetitionEvaluationService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RobotRepository robotRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    /**
     * Navrati skore vsech robotu, kteri soutezili v danem rocniku
     * 
     * @param year     Rocnik souteze
     * @param category Kategorie, pro kterou má zobrazit výsledky
     * @return Seznam vsech robotu a jejich skore v soutezi
     */
    public List<RobotScore> getScoreOfAll(int year, ECategory category) throws Exception {
        // overi zda rocnik souteze existuje
        Optional<Competition> competition = this.competitionRepository.findByYear(year);
        if (!competition.isPresent()) {
            throw new Exception(String.format("failure, competition [%d] not exists", year));
        }

        // pro vsechny registrovane tymy vypoci celkove skore u vsech jejich potvrzenych
        // robotu
        List<RobotScore> scoreList = new LinkedList<RobotScore>();

        List<TeamRegistration> registrations = competition.get().getRegistrations();
        for (TeamRegistration reg : registrations) {
            List<Robot> robots = reg.getRobots();
            for (Robot r : robots) {
                if (!r.getConfirmed() || r.getCategory() != category)
                    continue;

                // agregacni funkce skore
                ScoreAggregation ag = r.getDiscipline().getScoreAggregation();
                // funkci aplikuje pro vsechny zapasy, ktere robot odehral
                float totalScore = ag.getTotalScoreInitValue();
                List<RobotMatch> matches = r.getMatches();
                for (RobotMatch m : matches) {
                    if (m.getState().getName() == EMatchState.DONE) {
                        totalScore = ag.proccess(totalScore, m.getScore());
                    }
                }

                // score s robotem zapise do listu
                scoreList.add(new RobotScore(r, totalScore));
            }
        }

        return scoreList;
    }

    /**
     * Navrati skore vsech robotu urciteho tymu
     * 
     * @param year Rocnik
     * @param id   ID tymu
     * @return Navrati skore vsech reobotu v tymu
     */
    public TeamScore getScoreOfTeam(int year, long id) throws Exception {
        // overi zda tym existuje
        Optional<Team> team = this.teamRepository.findById(id);
        if (!team.isPresent()) {
            throw new Exception(String.format("failure, team with ID [%d] not exists", id));
        }

        // najde registraci pro dany rocnik souteze
        Optional<TeamRegistration> registration = team.get().getRegistrations().stream()
                .filter((r) -> (r.getCompatitionYear() == year)).findFirst();
        if (!registration.isPresent()) {
            throw new Exception(String.format("failure, registration for year [%d] not exists", year));
        }

        // pro kazdeho robota spocita skore
        List<RobotScore> scoreList = new ArrayList<RobotScore>();
        List<Robot> robots = registration.get().getRobots();

        robots.stream().forEach((r) -> {
            // prihlaska robota musi byt potvrzena
            if (r.getConfirmed()) {
                // agregacni funkce skore
                ScoreAggregation ag = r.getDiscipline().getScoreAggregation();
                // funkci aplikuje pro vsechny zapasy, ktere robot odehral
                float totalScore = ag.getTotalScoreInitValue();
                List<RobotMatch> matches = r.getMatches();
                for (RobotMatch m : matches) {
                    if (m.getState().getName() == EMatchState.DONE) {
                        totalScore = ag.proccess(totalScore, m.getScore());
                    }
                }
                scoreList.add(new RobotScore(r, totalScore));
            }
        });

        return new TeamScore(team.get(), scoreList);
    }

    /**
     * Navrati skore jednoho konkreniho robota
     * 
     * @param year Rocnik souteze
     * @param id   ID robota
     * @return Navrati skore robota
     */
    public RobotScore getScoreOfRobot(int year, long id) throws Exception {
        Optional<Robot> robot = this.robotRepository.findById(id);
        if (!robot.isPresent()) {
            throw new Exception(String.format("failure, robot with ID [%d] not exists", id));
        }

        // overi povrzeni robota
        if (!robot.get().getConfirmed()) {
            throw new Exception(String.format("failure, robot with ID [%d] is not confirmed", id));
        }

        // overi rocnik souteze
        if (robot.get().getTeamRegistration().getCompatitionYear() != year) {
            throw new Exception(String.format("failure, this robot is not registed in year [%d]", year));
        }

        // agregacni funkce skore
        ScoreAggregation ag = robot.get().getDiscipline().getScoreAggregation();
        // funkci aplikuje pro vsechny zapasy, ktere robot odehral
        float totalScore = ag.getTotalScoreInitValue();
        List<RobotMatch> matches = robot.get().getMatches();
        for (RobotMatch m : matches) {
            if (m.getState().getName() == EMatchState.DONE) {
                totalScore = ag.proccess(totalScore, m.getScore());
            }
        }

        return new RobotScore(robot.get(), totalScore);
    }

    /**
     * Navrati umisteni robotu v konkretni discipline v ramci soutezni kategorie
     * 
     * @param year     Rocnik souteze
     * @param category Soutezni kategorie
     * @param id       ID discipliny
     * @return Poradi vsech robotu, kteri soutezili v dane discipline + kategorii
     */
    public List<OrderObj> getOrder(int year, ECategory category, long id) throws Exception {
        // overi zda rocnik souteze existuje
        if (!this.competitionRepository.findByYear(year).isPresent()) {
            throw new Exception(String.format("failure, competition [%d] not exists", year));
        }

        // najde discipliny
        Optional<Discipline> discipline = this.disciplineRepository.findById(id);
        if (!discipline.isPresent()) {
            throw new Exception(String.format("failure, discipline with ID [%d] not exists", id));
        }

        // najde vsechny roboty v discipline, kteri hrali v danem roce
        Stream<Robot> robots = discipline.get().getRobots().stream()
                .filter((r) -> (r.getTeamRegistration().getCompatitionYear() == year));

        // agregacni funkce skore
        ScoreAggregation ag = discipline.get().getScoreAggregation();

        List<RobotScore> all = new LinkedList<>();
        robots.forEach(r -> {
            if (r.getConfirmed() && r.getCategory() == category) {
                // funkci aplikuje pro vsechny zapasy, ktere robot odehral
                float totalScore = ag.getTotalScoreInitValue();
                List<RobotMatch> matches = r.getMatches();
                for (RobotMatch m : matches) {
                    if (m.getState().getName() == EMatchState.DONE) {
                        totalScore = ag.proccess(totalScore, m.getScore());
                    }
                }

                // score s robotem zapise do listu
                all.add(new RobotScore(r, totalScore));
            }
        });

        // serazeni
        if (ag.getName() == EScoreAggregation.MIN) {
            // MIN -> od nejnizsi hodnoty skore po nejvestisi (line follower, drag race, ...
            // => score reprezenutje cas)
            Collections.sort(all, new Comparator<RobotScore>() {
                @Override
                public int compare(RobotScore r1, RobotScore r2) {
                    return r2.getScore() < r1.getScore() ? 1 : -1;
                }
            });
        } else {
            // MAX nebo SUM -> od nejvyssi po nejnizsi (sumo, robostrong, ..)
            Collections.sort(all, new Comparator<RobotScore>() {
                @Override
                public int compare(RobotScore r1, RobotScore r2) {
                    return r2.getScore() > r1.getScore() ? 1 : -1;
                }
            });
        }

        List<OrderObj> order = new LinkedList<OrderObj>();
        int place = 1;
        for (RobotScore scoreObj : all) {
            order.add(new OrderObj(place++, scoreObj));
        }

        // navrati viteze discipliny v dane soutezni kategorii
        return order;
    }

}
