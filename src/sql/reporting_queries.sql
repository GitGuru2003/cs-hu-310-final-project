/* Put your final project reporting queries here */
USE cs_hu_310_final_project;
-- 1 (complete)
SELECT
  students.first_name,
  students.last_name,
  COUNT(class_registrations.class_registration_id) AS number_of_classes,
  SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
  AVG(convert_to_grade_point(grades.letter_grade)) AS GPA
FROM class_registrations 
JOIN students ON class_registrations.student_id = students.student_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
WHERE students.student_id = 1
GROUP BY students.student_id, students.first_name, students.last_name;

-- 2
SELECT
  students.first_name,
  students.last_name,
  COUNT(class_registrations.class_registration_id) AS number_of_classes,
  SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
  AVG(convert_to_grade_point(grades.letter_grade)) AS GPA
FROM class_registrations 
JOIN students ON class_registrations.student_id = students.student_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY students.student_id, students.first_name, students.last_name;

-- 3
SELECT
  classes.code,
  classes.name,
  COUNT(grades.letter_grade) AS number_of_grades,
  SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
  AVG(convert_to_grade_point(grades.letter_grade)) AS AVG_GPA
FROM class_registrations 
JOIN class_sections ON class_registrations.class_section_id = class_sections.class_section_id
JOIN classes ON class_sections.class_id = classes.class_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY classes.class_id, classes.name;

-- 4
SELECT
  classes.code,
  classes.name,
  terms.name AS term,
  COUNT(grades.letter_grade) AS number_of_grades,
  SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
  AVG(convert_to_grade_point(grades.letter_grade)) AS avg_class_GPA
FROM class_registrations
JOIN class_sections ON class_registrations.class_section_id = class_sections.class_section_id
JOIN classes ON class_sections.class_id = classes.class_id
JOIN terms ON class_sections.term_id = terms.term_id
JOIN grades ON class_registrations.grade_id = grades.grade_id
GROUP BY classes.class_id, classes.name, terms.term_id, terms.name;

-- 5
SELECT
  instructors.first_name,
  instructors.last_name,
  academic_titles.title,
  classes.code,
  classes.name as class_name,
  terms.name as term
FROM class_sections
JOIN classes ON class_sections.class_id = classes.class_id
JOIN instructors ON class_sections.instructor_id = instructors.instructor_id
JOIN academic_titles ON instructors.academic_title_id = academic_titles.academic_title_id
JOIN terms ON class_sections.term_id = terms.term_id
WHERE instructors.instructor_id = 1;


-- 6
SELECT
  classes.code,
  classes.name,
  terms.name AS term,
  instructors.first_name,
  instructors.last_name
FROM class_sections
JOIN classes ON class_sections.class_id = classes.class_id
JOIN terms ON class_sections.term_id = terms.term_id
JOIN instructors ON class_sections.instructor_id = instructors.instructor_id;

-- 7 (try debugging this)
SELECT
  classes.code,
  classes.name,
  COUNT(class_registrations.class_registration_id) AS students_enrolled,
  classes.maximum_students - COUNT(class_registrations.class_registration_id) AS space_remaining
FROM class_sections
JOIN classes ON class_sections.class_id = classes.class_id
LEFT JOIN class_registrations ON class_sections.class_section_id = class_registrations.class_section_id
GROUP BY classes.class_id, classes.name, classes.maximum_students;
