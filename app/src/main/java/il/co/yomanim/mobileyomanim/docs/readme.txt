1. Журнал для родителя (ученика)
при кликабельных оценках (д\з, работа на уроке) необходимо показывать описание работы, которого нет в объекте StudentJournalItemModel
список объектов получаем по методу
marks/person/{person}/journalitems
передавая телом массив уроков, по которым хотим получить детализацию.
да, метод не возвращает работ, но они к нам приходят вместе с предшествующим запросом, когда получаем список уроков для журнала по диапазону дат:
person/{person}/lessons/{from}/{to}
возвращает список объектов Lesson, каждый содержит список работ на уроке, если они есть.
название типа работ определяется по соотношению тип – название, которое мы получаем методом GET: /events/{schoolId}
если в этом есть необходимость.

2. Есть баг в методе получения итоговых оценок для ученика, person/{person}/period/{period}/final-marks-student - не возвращается тип оценки
исправлено на бете

3. Метод POST:  /tasks не сохраняет изменений в статусе задания. Если передать задание со статусом “Completed” – ошибка.
Верно, статус “Completed” – означает, что работа выполнена учеником и отправлена на проверку учителю,
по сути этот статус предназначен для ученика а не для учителя. Как индикатор передачи выполненного задания на сторону учителя.
изменения в базе фиксируются
проверял на этой задаче
{"id":2924,"person":1000000009284,"status":"Discuss","targetDate":"Aug 24, 2016 12:00:00 AM","work":3339}
проблем не увидел


1. Timetalbe tabs date fixed
2. Removed 'Room:' title from each timetable tile
3. Homework: status dependencies


4. --> Move to the session:
public static String getReportingPeriods(Context context, String token, long schoolId)      +
public static String getSchoolTimetable(Context context, String token, long schoolId)       +
public static String getBehaviourStatusList(Context context, String token, long schoolId)   +
public static String getPresenceStatuses(Context context, String token)
4.1 --> clear schoolSession methods on change school (SchoolActivity)