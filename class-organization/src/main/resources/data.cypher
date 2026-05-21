//VOZILA

CREATE (: Vehicle {registrationNumber:'NS357SD',registrationExpiryDate: date('2027-01-01'), status: 'AVAILABLE',
                   currentMileage: 150000})

CREATE (v2: Vehicle {registrationNumber:'BG596JD',registrationExpiryDate: date('2029-11-09'), status: 'AVAILABLE',
                     currentMileage: 195000})

CREATE (v3: Vehicle {registrationNumber:'JA634BC',registrationExpiryDate: date('2026-09-08'), status: 'IN_USE',
                     currentMileage: 110000})

CREATE (v4: Vehicle {registrationNumber:'VA299GE',registrationExpiryDate: date('2027-02-11'), status: 'IN_USE',
                     currentMileage: 186500})

CREATE (v5: Vehicle {registrationNumber:'NS333GD',registrationExpiryDate: date('2028-03-03'), status: 'IN_USE',
                     currentMileage: 111000})

CREATE (v6: Vehicle {registrationNumber:'SA456UB',registrationExpiryDate: date('2026-01-01'), status: 'OUT_OF_SERVICE',
                     currentMileage: 200000})


//VREMENSKE PREFERENCE YA CAS

CREATE (tp1: TimePreference {date:date('2026-04-28'),startTime: time('08:00:00'), endTime: time('10:00:00') })

CREATE (tp2: TimePreference {date:date('2026-04-29'),startTime: time('09:00:00'), endTime: time('12:00:00') })

CREATE (tp3: TimePreference {date:date('2026-04-30'),startTime: time('11:00:00'), endTime: time('17:00:00') })


//PRAKTICNI CAS
CREATE (pc1:PracticalClass { startTime: datetime('2026-04-28T08:00:00'), endTime: datetime('2026-04-28T09:30:00'),completed: false})

CREATE (pc2:PracticalClass {startTime: datetime('2026-04-29T10:00:00'), endTime: datetime('2026-04-29T11:30:00'), completed: true})

CREATE (pc3:PracticalClass {startTime: datetime('2026-04-30T14:00:00'), endTime: datetime('2026-04-30T15:30:00'), completed: true})

CREATE (pc4:PracticalClass {startTime: datetime('2026-05-01T08:00:00'), endTime: datetime('2026-05-01T09:30:00'), completed: false})

CREATE (pc5:PracticalClass {startTime: datetime('2026-05-02T16:00:00'), endTime: datetime('2026-05-02T17:30:00'), completed: false})

//INSTRUKTORI

CREATE(i1:User:Instructor {username:'mikimilojevic', name:'Milan', lastname:'Milojevic', email:'milojevicm@gmail.com',maxCapacity:4})

CREATE(i2:User:Instructor {username:'jovanovick', name:'Katarina', lastname:'Jovanovic', email:'kjovanovic@gmail.com',maxCapacity:4})

CREATE(i3:User:Instructor {username:'lputaj', name:'Lela', lastname:'Putaj', email:'lputaj@gmail.com',maxCapacity:4})


//INST -[VOZI]-> VOZILO

MATCH (i:Instructor {username: 'mikimilojevic'}),(v:Vehicle {registrationNumber: 'JA634BC'})
CREATE (i) - [:DRIVES {assignedDate: datetime('2026-01-10T08:00:00'),
                       startMileage: 105000,
                       primary: true}] -> (v)


MATCH (i:Instructor {username: 'jovanovick'}),(v:Vehicle {registrationNumber: 'VA299GE'})
CREATE (i) - [:DRIVES {assignedDate: datetime('2026-02-15T09:00:00'),
                       startMileage: 180000,
                       primary: true}] -> (v)

MATCH (i:Instructor {username: 'lputaj'}),(v:Vehicle {registrationNumber: 'NS333GD'})
CREATE (i) - [:DRIVES {assignedDate: datetime('2026-03-01T07:30:00'),
                       startMileage: 110000,
                       primary: true}] -> (v)


//KANDIDAT

CREATE (c1:User:Candidate {username:'milicm', name:'Mila', lastname:'Milić', email:'milam@gmail.com', startOfTraining: datetime('2026-04-01T10:00:00'), theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c2:User:Candidate {username:'pericp', name:'Petar', lastname:'Perić', email:'pperic@gmail.com', startOfTraining: datetime('2026-04-05T09:00:00'), theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c3:User:Candidate {username:'markovics', name:'Sara', lastname:'Marković', email:'saram@gmail.com', startOfTraining: datetime('2026-04-10T11:00:00'), theoryCompleted: true, category: 'A', status: 'PRACTICAL'})

CREATE (c4:User:Candidate {username:'nikolicn', name:'Nikola', lastname:'Nikolić', email:'nnikolic@gmail.com', startOfTraining: datetime('2026-03-15T08:00:00'), theoryCompleted: true, category: 'C', status: 'PRACTICAL'})

CREATE (c5:User:Candidate {username:'lukicl', name:'Luka', lastname:'Lukić', email:'llukic@gmail.com', startOfTraining: datetime('2026-04-20T14:00:00'), theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c6:User:Candidate {username:'jovicm', name:'Milica', lastname:'Jović', email:'mjovic@gmail.com', startOfTraining: datetime('2026-02-01T10:00:00'), theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c7:User:Candidate {username:'anticd', name:'Dušan', lastname:'Antić', email:'dantic@gmail.com', startOfTraining: datetime('2026-04-22T09:30:00'), theoryCompleted: true, category: 'A2', status: 'PENDIPRACTICALNG'})




CREATE (c1:User:Candidate {username:'milicm', name:'Mila', lastname:'Milić', email:'milam@gmail.com', startOfTraining: '2026-04-01T10:00:00', theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c2:User:Candidate {username:'pericp', name:'Petar', lastname:'Perić', email:'pperic@gmail.com', startOfTraining: '2026-04-05T09:00:00', theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c3:User:Candidate {username:'markovics', name:'Sara', lastname:'Marković', email:'saram@gmail.com', startOfTraining: '2026-04-10T11:00:00', theoryCompleted: true, category: 'A', status: 'PRACTICAL'})

CREATE (c4:User:Candidate {username:'nikolicn', name:'Nikola', lastname:'Nikolić', email:'nnikolic@gmail.com', startOfTraining: '2026-03-15T08:00:00', theoryCompleted: true, category: 'C', status: 'PRACTICAL'})

CREATE (c5:User:Candidate {username:'lukicl', name:'Luka', lastname:'Lukić', email:'llukic@gmail.com', startOfTraining: '2026-04-20T14:00:00', theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c6:User:Candidate {username:'jovicm', name:'Milica', lastname:'Jović', email:'mjovic@gmail.com', startOfTraining: '2026-02-01T10:00:00', theoryCompleted: true, category: 'B', status: 'PRACTICAL'})

CREATE (c7:User:Candidate {username:'anticd', name:'Dušan', lastname:'Antić', email:'dantic@gmail.com', startOfTraining: '2026-04-22T09:30:00', theoryCompleted: true, category: 'A2', status: 'PRACTICAL'})

//INST - [TRAINS]-> KANDIDAT

MATCH (i:Instructor {username: 'mikimilojevic'}), (c1:Candidate {username: 'milicm'}), (c2:Candidate {username: 'pericp'})
CREATE (i)-[:TRAINS]->(c1)
CREATE (i)-[:TRAINS]->(c2)


MATCH (i:Instructor {username: 'jovanovick'}), (c3:Candidate {username: 'markovics'}), (c6:Candidate {username: 'jovicm'})
CREATE (i)-[:TRAINS]->(c3)
CREATE (i)-[:TRAINS]->(c6)

MATCH (i:Instructor {username: 'lputaj'}), (c4:Candidate {username: 'nikolicn'}), (c5:Candidate {username: 'lukicl'}),(c7:Candidate {username: 'anticd'})
CREATE (i)-[:TRAINS]->(c4)
CREATE (i)-[:TRAINS]->(c5)
CREATE (i)-[:TRAINS]->(c7)

//PREFERENCE KANDIDATA SA CASOVIMA ISTORIJA

//PRVI KANDIDAT
// preference
CREATE (tp1_c1:TimePreference {date: date('2026-03-02'), startTime: time('08:00:00'), endTime: time('10:00:00')})
CREATE (tp2_c1:TimePreference {date: date('2026-03-09'), startTime: time('08:00:00'), endTime: time('10:00:00')})
CREATE (tp3_c1:TimePreference {date: date('2026-03-16'), startTime: time('08:00:00'), endTime: time('10:00:00')})

// casovi
CREATE (pc1_c1:PracticalClass {startTime: datetime('2026-03-02T08:00:00'), completed: true})
CREATE (pc2_c1:PracticalClass {startTime: datetime('2026-03-10T14:00:00'), completed: true}) // Van termina
CREATE (pc3_c1:PracticalClass {startTime: datetime('2026-03-16T08:00:00'), completed: true})



//string
CREATE (tp1_c1:TimePreference {date: '2026-03-02', startTime: '08:00:00', endTime: '10:00:00'})
CREATE (tp2_c1:TimePreference {date: '2026-03-09', startTime: '08:00:00', endTime: '10:00:00'})
CREATE (tp3_c1:TimePreference {date: '2026-03-16', startTime: '08:00:00', endTime: '10:00:00'})

CREATE (pc1_c1:PracticalClass {startTime: '2026-03-02T08:00:00', completed: true})
CREATE (pc2_c1:PracticalClass {startTime: '2026-03-10T14:00:00', completed: true})
CREATE (pc3_c1:PracticalClass {startTime: '2026-03-16T08:00:00', completed: true})



//veye
MATCH (c:Candidate {username: 'milicm'}), (i:Instructor {username: 'mikimilojevic'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c1), (c)-[:HAS_PREFERENCE]->(tp2_c1), (c)-[:HAS_PREFERENCE]->(tp3_c1)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 12}]->(pc1_c1), (c)-[:ATTENDS {present: true, kmDriven: 10}]->(pc2_c1), (c)-[:ATTENDS {present: true, kmDriven: 15}]->(pc3_c1)
CREATE (i)-[:TEACHES {note: 'Redovna', score: 5}]->(pc1_c1), (i)-[:TEACHES {note: 'Naknadno ubačen', score: 4}]->(pc2_c1), (i)-[:TEACHES {note: 'Odlično', score: 5}]->(pc3_c1)


//DRUGI KANDIDAT

CREATE (tp1_c2:TimePreference {date: date('2026-03-03'), startTime: time('16:00:00')})
CREATE (tp2_c2:TimePreference {date: date('2026-03-10'), startTime: time('16:00:00')})

CREATE (pc1_c2:PracticalClass {startTime: datetime('2026-03-03T16:00:00'), completed: true})
CREATE (pc2_c2:PracticalClass {startTime: datetime('2026-03-11T08:00:00'), completed: true})

MATCH (c:Candidate {username: 'pericp'}), (i:Instructor {username: 'mikimilojevic'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c2), (c)-[:HAS_PREFERENCE]->(tp2_c2)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 15}]->(pc1_c2), (c)-[:ATTENDS {present: true, kmDriven: 12}]->(pc2_c2)
CREATE (i)-[:TEACHES {note: 'U terminu', score: 5}]->(pc1_c2), (i)-[:TEACHES {note: 'Morao rano ujutru', score: 3}]->(pc2_c2)


//TRECI
CREATE (tp1_c3:TimePreference {date: date('2026-03-04'), startTime: time('10:00:00')})
CREATE (pc1_c3:PracticalClass {startTime: datetime('2026-03-04T10:00:00'), completed: true})

MATCH (c:Candidate {username: 'markovics'}), (i:Instructor {username: 'jovanovick'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c3)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 11}]->(pc1_c3)
CREATE (i)-[:TEACHES {note: 'Prva vožnja', score: 5}]->(pc1_c3)


//CETRTI
CREATE (tp1_c4:TimePreference {date: date('2026-03-07'), startTime: time('07:00:00')})
CREATE (pc1_c4:PracticalClass {startTime: datetime('2026-03-07T07:00:00'), completed: true})

MATCH (c:Candidate {username: 'nikolicn'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c4)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 40}]->(pc1_c4)
CREATE (i)-[:TEACHES {note: 'Kamion poligon', score: 5}]->(pc1_c4)

//PETI

CREATE (tp1_c5:TimePreference {date: date('2026-03-05'), startTime: time('15:00:00')})
CREATE (pc1_c5:PracticalClass {startTime: datetime('2026-03-05T15:00:00'), completed: true})

MATCH (c:Candidate {username: 'lukicl'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c5)
CREATE (c)-[:ATTENDS {present: false, kmDriven: 0}]->(pc1_c5)
CREATE (i)-[:TEACHES {note: 'Nije došao', score: 0}]->(pc1_c5)


//SESTI

CREATE (tp1_c6:TimePreference {date: date('2026-03-06'), startTime: time('12:00:00')})
CREATE (pc1_c6:PracticalClass {startTime: datetime('2026-03-06T12:00:00'), completed: true})

MATCH (c:Candidate {username: 'jovicm'}), (i:Instructor {username: 'jovanovick'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c6)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 18}]->(pc1_c6)
CREATE (i)-[:TEACHES {note: 'Spremna za ispit', score: 5}]->(pc1_c6)

//SEDMI

CREATE (tp1_c7:TimePreference {date: date('2026-03-08'), startTime: time('18:00:00')})
CREATE (pc1_c7:PracticalClass {startTime: datetime('2026-03-08T18:00:00'), completed: true})

MATCH (c:Candidate {username: 'anticd'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c7)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 20}]->(pc1_c7)
CREATE (i)-[:TEACHES {note: 'Noćna vožnja', score: 5}]->(pc1_c7)


//string
// DRUGI KANDIDAT (pericp)
CREATE (tp1_c2:TimePreference {date: '2026-03-03', startTime: '16:00:00'})
CREATE (tp2_c2:TimePreference {date: '2026-03-10', startTime: '16:00:00'})

CREATE (pc1_c2:PracticalClass {startTime: '2026-03-03T16:00:00', completed: true})
CREATE (pc2_c2:PracticalClass {startTime: '2026-03-11T08:00:00', completed: true})

MATCH (c:Candidate {username: 'pericp'}), (i:Instructor {username: 'mikimilojevic'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c2), (c)-[:HAS_PREFERENCE]->(tp2_c2)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 15}]->(pc1_c2), (c)-[:ATTENDS {present: true, kmDriven: 12}]->(pc2_c2)
CREATE (i)-[:TEACHES {note: 'U terminu', score: 5}]->(pc1_c2), (i)-[:TEACHES {note: 'Morao rano ujutru', score: 3}]->(pc2_c2);


// TRECI (markovics)
CREATE (tp1_c3:TimePreference {date: '2026-03-04', startTime: '10:00:00'})
CREATE (pc1_c3:PracticalClass {startTime: '2026-03-04T10:00:00', completed: true})

MATCH (c:Candidate {username: 'markovics'}), (i:Instructor {username: 'jovanovick'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c3)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 11}]->(pc1_c3)
CREATE (i)-[:TEACHES {note: 'Prva vožnja', score: 5}]->(pc1_c3);


// CETRTI (nikolicn)
CREATE (tp1_c4:TimePreference {date: '2026-03-07', startTime: '07:00:00'})
CREATE (pc1_c4:PracticalClass {startTime: '2026-03-07T07:00:00', completed: true})

MATCH (c:Candidate {username: 'nikolicn'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c4)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 40}]->(pc1_c4)
CREATE (i)-[:TEACHES {note: 'Kamion poligon', score: 5}]->(pc1_c4);


// PETI (lukicl)
CREATE (tp1_c5:TimePreference {date: '2026-03-05', startTime: '15:00:00'})
CREATE (pc1_c5:PracticalClass {startTime: '2026-03-05T15:00:00', completed: true})

MATCH (c:Candidate {username: 'lukicl'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c5)
CREATE (c)-[:ATTENDS {present: false, kmDriven: 0}]->(pc1_c5)
CREATE (i)-[:TEACHES {note: 'Nije došao', score: 0}]->(pc1_c5);


// SESTI (jovicm)
CREATE (tp1_c6:TimePreference {date: '2026-03-06', startTime: '12:00:00'})
CREATE (pc1_c6:PracticalClass {startTime: '2026-03-06T12:00:00', completed: true})

MATCH (c:Candidate {username: 'jovicm'}), (i:Instructor {username: 'jovanovick'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c6)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 18}]->(pc1_c6)
CREATE (i)-[:TEACHES {note: 'Spremna za ispit', score: 5}]->(pc1_c6);


// SEDMI (anticd)
CREATE (tp1_c7:TimePreference {date: '2026-03-08', startTime: '18:00:00'})
CREATE (pc1_c7:PracticalClass {startTime: '2026-03-08T18:00:00', completed: true})

MATCH (c:Candidate {username: 'anticd'}), (i:Instructor {username: 'lputaj'})
CREATE (c)-[:HAS_PREFERENCE]->(tp1_c7)
CREATE (c)-[:ATTENDS {present: true, kmDriven: 20}]->(pc1_c7)
CREATE (i)-[:TEACHES {note: 'Noćna vožnja', score: 5}]->(pc1_c7);