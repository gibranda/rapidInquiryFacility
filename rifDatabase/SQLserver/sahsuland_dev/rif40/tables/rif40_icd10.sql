USE [sahsuland_dev]
GO

IF EXISTS (SELECT * FROM sys.objects 
WHERE object_id = OBJECT_ID(N'[rif40].[rif40_icd10]') AND type in (N'U'))
BEGIN
	DROP TABLE [rif40].[rif40_icd10]
END
GO

CREATE TABLE [rif40].[rif40_icd10](
	[icd10_1char] [varchar](20) NULL,
	[icd10_3char] [varchar](3) NULL,
	[icd10_4char] [varchar](4) NOT NULL,
	[text_1char] [varchar](250) NULL,
	[text_3char] [varchar](250) NULL,
	[text_4char] [varchar](250) NULL,
 CONSTRAINT [rif40_icd10_pk] PRIMARY KEY CLUSTERED 
(
	[icd10_4char] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

GRANT SELECT ON [rif40].[rif40_icd10] TO public
GO

CREATE INDEX rif40_icd10_1char_bm
  ON [rif40].[rif40_icd10] (icd10_1char)
GO

CREATE INDEX rif40_icd10_3char_bm
  ON [rif40].[rif40_icd10] (icd10_3char)
GO